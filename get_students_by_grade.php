<?php
// get_students_by_grade.php

header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");

// إعداد الاتصال بقاعدة البيانات
$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode([
        'status' => 'error',
        'message' => 'Database connection failed: ' . $conn->connect_error
    ]));
}

// استلام الصف المطلوب
$class_grade = isset($_GET['class_grade']) ? intval($_GET['class_grade']) : 0;

if ($class_grade <= 0) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid or missing class grade'
    ]);
    exit;
}

// جلب الطلاب من الصف المطلوب
$sql = "SELECT id AS student_id, full_name FROM users WHERE class_grade = ? AND role = 'Student'";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $class_grade);
$stmt->execute();
$result = $stmt->get_result();

$students = [];
while ($row = $result->fetch_assoc()) {
    $students[] = $row;
}

echo json_encode($students);

// إغلاق الاتصال
$stmt->close();
$conn->close();
?>
