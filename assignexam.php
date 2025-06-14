<?php
$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$subject_id = $_POST['subject_id'] ?? '';
$grade = $_POST['grade'] ?? '';
$exam_type = $_POST['exam_type'] ?? '';
$exam_date = $_POST['exam_date'] ?? '';
$description = $_POST['description'] ?? '';

if (empty($subject_id) || empty($grade) || empty($exam_type) || empty($exam_date)) {
    echo "Missing required fields";
    exit;
}

$today = date('Y-m-d');
if ($exam_date <= $today) {
    echo "Exam date must be after today";
    exit;
}

$check1 = $conn->prepare("SELECT COUNT(*) FROM exams WHERE grade = ? AND exam_date = ?");
$check1->bind_param("is", $grade, $exam_date);
$check1->execute();
$check1->bind_result($count);
$check1->fetch();
$check1->close();

if ($count >= 2) {
    echo "Cannot assign more than 2 exams on the same day for the same grade";
    exit;
}

$check2 = $conn->prepare("SELECT exam_id FROM exams WHERE grade = ? AND subject_id = ? AND exam_date = ?");
$check2->bind_param("iis", $grade, $subject_id, $exam_date);
$check2->execute();
$check2->store_result();
if ($check2->num_rows > 0) {
    echo "This subject already has an exam on this date for this grade";
    exit;
}
$check2->close();

$examTypePriority = [
    "First Exam" => 1,
    "Midterm Exam" => 2,
    "Second Exam" => 3,
    "Final Exam" => 4
];
$currentPriority = $examTypePriority[$exam_type];

foreach ($examTypePriority as $type => $priority) {
    if ($type == $exam_type) continue; 

    $checkGap = $conn->prepare("SELECT exam_date FROM exams 
        WHERE grade = ? AND subject_id = ? AND exam_type = ?");
    $checkGap->bind_param("iis", $grade, $subject_id, $type);
    $checkGap->execute();
    $result = $checkGap->get_result();
    while ($row = $result->fetch_assoc()) {
        $existingDate = $row['exam_date'];
        $date1 = new DateTime($exam_date);
        $date2 = new DateTime($existingDate);
        $interval = $date1->diff($date2)->days;

        if ($interval < 21) { 
            echo "There must be at least 3 weeks between \"$type\" and \"$exam_type\" exams";
            exit;
        }
    }
    $checkGap->close();
}

$stmt = $conn->prepare("INSERT INTO exams (subject_id, grade, exam_type, exam_date, description, created_at) 
                        VALUES (?, ?, ?, ?, ?, NOW())");
$stmt->bind_param("iisss", $subject_id, $grade, $exam_type, $exam_date, $description);

if ($stmt->execute()) {
    echo "Exam assigned successfully";
} else {
    echo "Error: " . $stmt->error;
}

$stmt->close();
$conn->close();
?>
