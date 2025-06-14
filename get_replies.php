<?php
header("Content-Type: application/json");
$conn = new mysqli("localhost", "root", "", "schoolsystem");

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit;
}

// جلب جميع الردود وربطها بالرسائل واسم الطالب
$sql = "SELECT 
            r.reply_content, 
            r.reply_date, 
            u.full_name AS student_name, 
            m.title AS message_title 
        FROM message_replies r
        JOIN users u ON r.reply_sender_id = u.id
        JOIN messages m ON r.message_id = m.id
        ORDER BY r.reply_date DESC";

$result = $conn->query($sql);
$data = [];

if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
    echo json_encode(["status" => "success", "data" => $data]);
} else {
    echo json_encode(["status" => "error", "message" => "No replies found"]);
}

$conn->close();
?>
