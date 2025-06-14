<?php
header("Content-Type: application/json");

// بيانات الاتصال بقاعدة البيانات
$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

// الاتصال
$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit();
}

$student_id = $_POST['student_id'] ?? 0;
$survey_id  = $_POST['survey_id'] ?? 0;
$answer     = $_POST['answer'] ?? '';

if (!$student_id || !$survey_id || empty($answer)) {
    echo json_encode(["status" => "error", "message" => "Missing data"]);
    exit();
}

// تحقق إذا الحالة في جدول surveys = Answered
$checkSurvey = $conn->prepare("SELECT status FROM surveys WHERE id = ?");
$checkSurvey->bind_param("i", $survey_id);
$checkSurvey->execute();
$result = $checkSurvey->get_result();
$survey = $result->fetch_assoc();
$checkSurvey->close();

if (!$survey) {
    echo json_encode(["status" => "error", "message" => "Survey not found"]);
    exit();
}

if (strtolower($survey['status']) === 'answered') {
    echo json_encode(["status" => "error", "message" => "You have already answered this survey"]);
    exit();
}

// إدخال الإجابة
$stmt = $conn->prepare("INSERT INTO survey_answers (student_id, survey_id, answer) VALUES (?, ?, ?)");
$stmt->bind_param("iis", $student_id, $survey_id, $answer);

if ($stmt->execute()) {
    // ✅ تحديث حالة الاستبيان
    $update = $conn->prepare("UPDATE surveys SET status = 'Answered' WHERE id = ?");
    $update->bind_param("i", $survey_id);
    $update->execute();
    $update->close();

    echo json_encode(["status" => "success", "message" => "Answer submitted successfully"]);
} else {
    echo json_encode(["status" => "error", "message" => "Failed to save answer"]);
}

$stmt->close();
$conn->close();
?>
