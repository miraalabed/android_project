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

// التحقق من وجود grade في الرابط
if (!isset($_GET['grade'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing grade parameter"
    ]);
    exit();
}

$grade = intval($_GET['grade']);

// الاستعلام مع join على جدول المواد
$sql = "
    SELECT exams.exam_id, exams.exam_type, exams.exam_date, exams.description, subjects.subject_name
    FROM exams
    INNER JOIN subjects ON exams.subject_id = subjects.subject_id
    WHERE exams.grade = ?
    ORDER BY exams.exam_date ASC
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $grade);
$stmt->execute();
$result = $stmt->get_result();

// تجهيز البيانات
$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = [
        "exam_id" => $row["exam_id"],
        "subject_name" => $row["subject_name"],
        "exam_type" => $row["exam_type"],
        "exam_date" => $row["exam_date"],
        "description" => $row["description"]
    ];
}

// الإخراج النهائي
echo json_encode([
    "status" => "success",
    "exams" => $data
]);

// إغلاق الاتصال
$conn->close();
?>
