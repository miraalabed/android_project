<?php
// إعداد الهيدر لملف JSON
header("Content-Type: application/json");

// الاتصال بقاعدة البيانات
$conn = new mysqli("localhost", "root", "", "schoolsystem");

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit;
}

// الحصول على assignment_id من الرابط
$assignment_id = $_GET['assignment_id'] ?? null;

if (!$assignment_id) {
    echo json_encode(["status" => "error", "message" => "Missing assignment_id"]);
    exit;
}

// استعلام لجلب الردود مع اسم الطالب (تأكد من اسم العمود الصحيح في جدول users)
$sql = "SELECT ar.reply_text, ar.reply_link, u.full_name AS student_name
        FROM assignment_replies ar
        JOIN users u ON ar.student_id = u.id
        WHERE ar.assignment_id = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $assignment_id);
$stmt->execute();
$result = $stmt->get_result();

// جمع النتائج
$replies = [];
while ($row = $result->fetch_assoc()) {
    $replies[] = $row;
}

// إخراج JSON
echo json_encode([
    "status" => "success",
    "data" => $replies
]);

// إغلاق الاتصال
$stmt->close();
$conn->close();
