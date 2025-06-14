<?php
header("Content-Type: application/json");
ini_set('display_errors', 1);
error_reporting(E_ALL);

// الاتصال بقاعدة البيانات
$conn = new mysqli("localhost", "root", "", "schoolsystem");
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit();
}

// جلب البيانات من POST
$sender_id     = $_POST['sender_id']     ?? '';
$sender_type   = $_POST['sender_type']   ?? '';
$receiver_id   = $_POST['receiver_id']   ?? '';
$receiver_type = $_POST['receiver_type'] ?? 'Student';
$title         = $_POST['title']         ?? '';
$content       = $_POST['content']       ?? '';

// التحقق من الحقول
if (empty($sender_id) || empty($sender_type) || empty($receiver_id) || empty($title) || empty($content)) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing or invalid fields",
        "received" => $_POST
    ]);
    exit();
}

// إضافة Log لتأكيد البيانات المُرسلة
file_put_contents("debug_log.txt", "DATA RECEIVED: " . json_encode($_POST) . "\n", FILE_APPEND);

// تنفيذ الإدخال بدون حقل id
$sql = "INSERT INTO messages (sender_id, sender_type, receiver_id, receiver_type, title, content) 
        VALUES (?, ?, ?, ?, ?, ?)";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode([
        "status" => "error",
        "message" => "Prepare failed",
        "error" => $conn->error
    ]);
    exit();
}

$stmt->bind_param("isssss", $sender_id, $sender_type, $receiver_id, $receiver_type, $title, $content);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Message sent"]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Insert failed",
        "error" => $stmt->error
    ]);

    // حفظ الخطأ في ملف
    file_put_contents("debug_log.txt", "INSERT ERROR: " . $stmt->error . "\n", FILE_APPEND);
}

$stmt->close();
$conn->close();
?>
