<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
require_once 'db_config.php';

if (!isset($_GET['email']) || empty($_GET['email'])) {
    echo json_encode(['success' => false, 'message' => 'Email is required']);
    exit;
}

$email = $_GET['email'];

// جلب teacher_id من users عبر الإيميل
$sql_teacher = "SELECT id FROM users WHERE email = ? AND role = 'Teacher' LIMIT 1";
$stmt_teacher = mysqli_prepare($link, $sql_teacher);
mysqli_stmt_bind_param($stmt_teacher, "s", $email);
mysqli_stmt_execute($stmt_teacher);
$result_teacher = mysqli_stmt_get_result($stmt_teacher);
$teacher = mysqli_fetch_assoc($result_teacher);

if (!$teacher) {
    echo json_encode(['success' => false, 'message' => 'Teacher not found']);
    exit;
}

$teacher_id = $teacher['id'];

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
mysqli_stmt_bind_param($stmt, "i", $teacher_id);
mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);
$schedule = mysqli_fetch_all($result, MYSQLI_ASSOC);

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
        'room_name' => 'N/A'
    ];
}

echo json_encode([
    'success' => true,
    'schedule' => $organized_schedule
]);
?>
