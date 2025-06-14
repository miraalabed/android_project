<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST");
header("Access-Control-Allow-Headers: Content-Type");

$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode([
        'status' => 'error',
        'message' => 'Connection failed: ' . $conn->connect_error
    ]));
}

// جلب جميع المواد مع معلومات المعلمين
$sql = "SELECT s.subject_id, s.subject_name, s.description, s.teacher_id, 
               u.full_name AS teacher_name
        FROM subjects s
        LEFT JOIN users u ON s.teacher_id = u.id";

$result = $conn->query($sql);

if (!$result) {
    die(json_encode([
        'status' => 'error',
        'message' => 'Query failed: ' . $conn->error
    ]));
}

$subjects = [];
while ($row = $result->fetch_assoc()) {
    // التأكد من وجود teacher_id
    if ($row['teacher_id'] === null) {
        $row['teacher_id'] = 0; // أو أي قيمة افتراضية مناسبة
    }
    $subjects[] = $row;
}

echo json_encode($subjects);

$conn->close();
?>