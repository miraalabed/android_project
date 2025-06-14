<?php
$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die(json_encode([]));
}

$survey_id = $_GET['survey_id'] ?? 0;

$stmt = $conn->prepare("SELECT u.full_name, sa.submitted_at 
                        FROM survey_answers sa 
                        JOIN users u ON sa.student_id = u.id 
                        WHERE sa.survey_id = ?");
$stmt->bind_param("i", $survey_id);
$stmt->execute();
$result = $stmt->get_result();

$students = [];
while ($row = $result->fetch_assoc()) {
    $students[] = $row;
}

header('Content-Type: application/json');
echo json_encode($students);
$conn->close();
?>
