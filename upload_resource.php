<?php
// إعداد الهيدر
header("Content-Type: application/json");
ini_set('display_errors', 1);
error_reporting(E_ALL);

// الاتصال بقاعدة البيانات
$conn = new mysqli("localhost", "root", "", "schoolsystem");

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit;
}

// جلب البيانات من POST
$class_grade = $_POST['class_grade'] ?? '';
$title = $_POST['title'] ?? '';
$description = $_POST['description'] ?? '';
$file_link = $_POST['file_link'] ?? ''; // استلام الرابط إذا موجود
$date = date('Y-m-d'); // التاريخ الحالي

// التحقق من تعبئة البيانات الأساسية
if (empty($class_grade) || empty($title) || empty($description)) {
    echo json_encode(["status" => "error", "message" => "Missing fields"]);
    exit;
}

// تنفيذ عملية الإدخال
$stmt = $conn->prepare("INSERT INTO learning_resources (class_grade, title, description, file_link, date) VALUES (?, ?, ?, ?, ?)");
$stmt->bind_param("sssss", $class_grade, $title, $description, $file_link, $date);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Resource uploaded"]);
} else {
    echo json_encode(["status" => "error", "message" => "Upload failed: " . $conn->error]);
}

$stmt->close();
$conn->close();
?>
