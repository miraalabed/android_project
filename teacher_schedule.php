<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET");

require_once 'db_config.php';


error_log("Received request for teacher schedule. GET params: " . print_r($_GET, true));

if (!isset($_GET['teacher_id']) || empty($_GET['teacher_id'])) {
    send_json_error("Teacher ID is required", 400);
}

$teacher_id = (int)$_GET['teacher_id'];
error_log("Processing schedule for teacher ID: " . $teacher_id);

if ($teacher_id <= 0) {
    send_json_error("Invalid Teacher ID", 400);
}

try {

    $sql = "
        SELECT 
            s.schedule_id,
            s.day_of_week,
            s.start_time,
            s.end_time,
            sub.subject_name,
            s.class_grade,
            s.teacher_id
        FROM 
            teacherschedule s
        JOIN 
            subjects sub ON s.subject_id = sub.subject_id
        WHERE 
            s.teacher_id = ?
        ORDER BY 
            FIELD(s.day_of_week, 'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'),
            s.start_time
    ";

    $stmt = mysqli_prepare($link, $sql);
    if (!$stmt) {
        throw new Exception("Database prepare error: " . mysqli_error($link));
    }

    mysqli_stmt_bind_param($stmt, "i", $teacher_id);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);
    $schedule = mysqli_fetch_all($result, MYSQLI_ASSOC);

    if (empty($schedule)) {
        send_json_response([
            'success' => true,
            'message' => 'No schedule found for this teacher',
            'schedule' => []
        ]);
    } else {
        $organized_schedule = [];
        foreach ($schedule as $item) {
            $day = $item['day_of_week'];
            if (!isset($organized_schedule[$day])) {
                $organized_schedule[$day] = [];
            }
            $organized_schedule[$day][] = [
                'subject_name' => $item['subject_name'],
                'class_name' => 'Grade ' . $item['class_grade'],
                'start_time' => $item['start_time'],
                'end_time' => $item['end_time'],
                'teacher_id' => $item['teacher_id'],
                'room_name' => 'N/A' // Added placeholder for room_name
            ];
        }

        send_json_response([
            'success' => true,
            'message' => 'Schedule retrieved successfully',
            'schedule' => $organized_schedule
        ]);
    }

} catch (Exception $e) {
    error_log("Error: " . $e->getMessage());
    send_json_error("Server error: " . $e->getMessage(), 500);
}
?>

