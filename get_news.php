<?php
// لعرض الأخطاء أثناء التطوير (احذفهم في الإنتاج)
ini_set('display_errors', 1);
error_reporting(E_ALL);

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

// التحقق من الاتصال
if ($conn->connect_error) {
    echo json_encode([
        "status" => "error",
        "message" => "Connection failed: " . $conn->connect_error
    ]);
    exit();
}

// تنفيذ الاستعلام
$sql = "SELECT * FROM news ORDER BY date DESC";
$result = $conn->query($sql);

// تجهيز البيانات
$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = [
        "title" => $row["title"],
        "content" => $row["content"],
        "date" => $row["date"]
    ];
}

// الإخراج النهائي
echo json_encode([
    "status" => "success",
    "data" => $data
]);

// إغلاق الاتصال
$conn->close();
?>
