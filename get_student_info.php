<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");

// الاتصال بقاعدة البيانات
$conn = new mysqli("localhost", "root", "", "schoolsystem");

// التحقق من الاتصال
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit;
}

// استقبال student_id من GET
$student_id = $_GET['student_id'] ?? null;
if (!$student_id) {
    echo json_encode(["status" => "error", "message" => "Missing student_id"]);
    exit;
}

// الاستعلام للحصول على بيانات الطالب
$sql = "SELECT name, email, national_id, phone, address FROM students WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $student_id);  // نستخدم "i" لأننا نرسل قيمة من نوع INT

$stmt->execute();
$result = $stmt->get_result();

// التحقق إذا كانت البيانات موجودة
if ($row = $result->fetch_assoc()) {
    // إرجاع البيانات في تنسيق JSON
    echo json_encode(["status" => "success", "data" => $row]);
} else {
    // إرجاع رسالة إذا لم يتم العثور على الطالب
    echo json_encode(["status" => "error", "message" => "Student not found"]);
}

// إغلاق الاتصال
$stmt->close();
$conn->close();
?>
