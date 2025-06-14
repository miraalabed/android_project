<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
header('Content-Type: application/json');

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "schoolsystem";

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode([
        "status" => "error",
        "message" => "Database connection failed: " . $conn->connect_error
    ]);
    exit();
}

$rawData = file_get_contents("php://input");
$data = json_decode($rawData, true);

if (!$data || !isset($data['schedule'])) {
    http_response_code(400);
    echo json_encode([
        "status" => "error",
        "message" => "Invalid or missing schedule data"
    ]);
    exit();
}

$schedule = $data['schedule'];

if (empty($schedule)) {
    http_response_code(400);
    echo json_encode([
        "status" => "error",
        "message" => "Empty schedule array"
    ]);
    exit();
}

$successCount = 0;
$errors = [];

$class_grade = $schedule[0]['class_grade'];
$delete_sql = "DELETE FROM class_schedule WHERE class_grade = ?";
$del_stmt = $conn->prepare($delete_sql);

if ($del_stmt) {
    $del_stmt->bind_param("i", $class_grade);
    if (!$del_stmt->execute()) {
        $errors[] = "Failed to delete old schedule: " . $del_stmt->error;
    }
    $del_stmt->close();
} else {
    $errors[] = "Delete prepare failed: " . $conn->error;
}

$stmt = $conn->prepare("INSERT INTO class_schedule (class_grade, day, period_number, subject_id, teacher_id, end_time, start_time) VALUES (?, ?, ?, ?, ?, ?, ?)");

if ($stmt) {
    foreach ($schedule as $item) {
        $class_grade = $item['class_grade'] ?? null;
        $day = $item['day'] ?? null;
        $period_number = $item['period_number'] ?? null;
        $subject_id = $item['subject_id'] ?? null;
        $teacher_id = $item['teacher_id'] ?? null;
        $start_time = $item['start_time'] ?? null;
        $end_time = $item['end_time'] ?? null;

        if (!$class_grade || !$day || !$period_number || !$subject_id || !$teacher_id || !$start_time || !$end_time) {
            $errors[] = "Missing required fields in schedule item";
            continue;
        }

        // ✅ تعديل ترتيب الحقول هنا
        $stmt->bind_param("isiiiss", $class_grade, $day, $period_number, $subject_id, $teacher_id, $end_time, $start_time);

        if ($stmt->execute()) {
            $successCount++;
        } else {
            $errors[] = "Insert failed: " . $stmt->error;
        }
    }
    $stmt->close();
} else {
    $errors[] = "Insert prepare failed: " . $conn->error;
}

$conn->close();

if (empty($errors)) {
    echo json_encode([
        "status" => "success",
        "message" => "Student schedule saved successfully",
        "inserted" => $successCount,
        "total" => count($schedule)
    ]);
} else {
    http_response_code(500);
    echo json_encode([
        "status" => "partial",
        "message" => "Some schedule items were not saved",
        "inserted" => $successCount,
        "total" => count($schedule),
        "errors" => $errors
    ]);
}
?>
