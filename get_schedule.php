<?php
header("Content-Type: application/json");
ini_set('display_errors', 1);
error_reporting(E_ALL);

// الاتصال بقاعدة البيانات بدون تحديد بورت
$conn = new mysqli("localhost", "root", "", "schoolsystem");

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Database connection failed"]);
    exit();
}

$class_grade = $_GET['class_grade'] ?? '';
if (empty($class_grade)) {
    echo json_encode(["status" => "error", "message" => "Missing class_grade"]);
    exit();
}

// الاستعلام مع ترتيب الأيام
$sql = "
    SELECT 
        cs.schedule_id,
        cs.class_grade,
        cs.day,
        cs.period_number,
        cs.start_time,
        cs.end_time,
        s.subject_name,
        t.teacher_name
    FROM class_schedule cs
    JOIN subjects s ON cs.subject_id = s.subject_id
    JOIN teachers t ON cs.teacher_id = t.teacher_id
    WHERE cs.class_grade = ?
    ORDER BY 
        FIELD(cs.day, 'Saturday', 'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'),
        cs.period_number
";

$stmt = $conn->prepare($sql);
if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "SQL error: " . $conn->error]);
    exit();
}

$stmt->bind_param("i", $class_grade);
$stmt->execute();
$result = $stmt->get_result();

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode([
    "status" => "success",
    "data" => $data
]);

$stmt->close();
$conn->close();