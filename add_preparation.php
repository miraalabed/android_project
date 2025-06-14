<?php
header("Content-Type: application/json");

$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit();
}

// استلام البيانات من POST
$class = $_POST['class_grade'] ?? '';
$subject = $_POST['subject_name'] ?? '';
$title = $_POST['title'] ?? '';
$content = $_POST['content'] ?? '';
$date = date('Y-m-d'); // أو استلمي من POST إذا بدك

// تأكد من البيانات
if (empty($class) || empty($subject) || empty($title) || empty($content)) {
    echo json_encode(["status" => "error", "message" => "Missing data"]);
    exit();
}

// تنفيذ الإدخال
$stmt = $conn->prepare("INSERT INTO preparation_plans (class_grade, subject_name, title, content, date) VALUES (?, ?, ?, ?, ?)");
$stmt->bind_param("sssss", $class, $subject, $title, $content, $date);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Plan added"]);
} else {
    echo json_encode(["status" => "error", "message" => "Insert failed"]);
}

$stmt->close();
$conn->close();
?>