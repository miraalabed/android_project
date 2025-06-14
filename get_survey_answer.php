<?php
$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed"]));
}

$survey_id = $_GET['survey_id'] ?? 0;
$student_name = $_GET['student_name'] ?? '';

if (empty($survey_id) || empty($student_name)) {
    echo json_encode(["error" => "Missing parameters"]);
    exit;
}

$stmtUser = $conn->prepare("SELECT id FROM users WHERE full_name = ?");
$stmtUser->bind_param("s", $student_name);
$stmtUser->execute();
$resultUser = $stmtUser->get_result();
if ($resultUser->num_rows == 0) {
    echo json_encode(["error" => "Student not found"]);
    exit;
}
$student_row = $resultUser->fetch_assoc();
$student_id = $student_row['id'];

$stmt = $conn->prepare("
    SELECT s.title, s.question, sa.answer
    FROM survey_answers sa
    JOIN surveys s ON sa.survey_id = s.id
    WHERE sa.survey_id = ? AND sa.student_id = ?
");
$stmt->bind_param("ii", $survey_id, $student_id);
$stmt->execute();
$result = $stmt->get_result();

$answers = [];
while ($row = $result->fetch_assoc()) {
    $answers[] = $row;
}

header('Content-Type: application/json');
echo json_encode($answers);
$conn->close();
?>
