<?php
// عرض الأخطاء لتشخيص أي مشكلة
error_reporting(E_ALL);
ini_set('display_errors', 1);

// إعداد الهيدر للإخراج كـ JSON
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");

// بيانات الاتصال بقاعدة البيانات
$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

// الاتصال
$conn = new mysqli($host, $user, $pass, $db);

// فحص الاتصال
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit;
}

// جلب student_id من الرابط
$student_id = $_GET['student_id'] ?? null;

if (!$student_id) {
    echo json_encode(["status" => "error", "message" => "Missing student_id"]);
    exit;
}

// 1. جلب class_grade الخاص بالطالب
$grade_sql = "SELECT class_grade FROM users WHERE id = ?";
$stmt = $conn->prepare($grade_sql);
$stmt->bind_param("i", $student_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "Student not found"]);
    exit;
}

$row = $result->fetch_assoc();
$class_grade = $row['class_grade'];
$stmt->close();

// 2. جلب الأسايمنتات الخاصة بهذا الصف فقط
$sql = "SELECT assignment_id, title, description, due_date 
        FROM assignments 
        WHERE class_grade = ?
        ORDER BY due_date ASC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $class_grade);
$stmt->execute();
$res = $stmt->get_result();

$assignments = [];

while ($row = $res->fetch_assoc()) {
    $assignments[] = array_map('htmlspecialchars', $row);
}

echo json_encode([
    "status" => "success",
    "data" => $assignments
]);

$stmt->close();
$conn->close();
?>
