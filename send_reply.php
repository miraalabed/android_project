<?php
header("Content-Type: application/json");

$conn = new mysqli("localhost", "root", "", "schoolsystem");
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit();
}

$message_id = $_POST['message_id'] ?? '';
$reply_content = $_POST['reply_content'] ?? '';
$reply_sender_id = $_POST['reply_sender_id'] ?? '';
$reply_sender_type = $_POST['reply_sender_type'] ?? 'Student';

// التحقق من الحقول المطلوبة
if (empty($message_id) || empty($reply_content) || empty($reply_sender_id)) {
    echo json_encode(["status" => "error", "message" => "Missing fields"]);
    exit();
}

// تجهيز الاستعلام
$sql = "INSERT INTO message_replies (message_id, reply_content, reply_sender_id, reply_sender_type, reply_date)
        VALUES (?, ?, ?, ?, NOW())";
$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "Prepare failed: " . $conn->error]);
    exit();
}

$stmt->bind_param("isis", $message_id, $reply_content, $reply_sender_id, $reply_sender_type);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Reply saved successfully"]);
} else {
    echo json_encode(["status" => "error", "message" => "Execute failed: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>