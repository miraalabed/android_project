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
$assignment_id = $_POST['assignment_id'] ?? null;
$student_id = $_POST['student_id'] ?? null;
$reply_text = $_POST['reply_content'] ?? ''; // ✅ تعديل الاسم هنا
$reply_link = $_POST['reply_link'] ?? '';

if (!$assignment_id || !$student_id) {
    echo json_encode(["status" => "error", "message" => "Missing data"]);
    exit;
}

// تنفيذ عملية الإدخال
$stmt = $conn->prepare("INSERT INTO assignment_replies (assignment_id, student_id, reply_text, reply_link) VALUES (?, ?, ?, ?)");
$stmt->bind_param("iiss", $assignment_id, $student_id, $reply_text, $reply_link);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Reply submitted"]);
} else {
    echo json_encode(["status" => "error", "message" => "Insert failed: " . $conn->error]);
}

$stmt->close();
$conn->close();
?>