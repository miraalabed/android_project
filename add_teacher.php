<?php
$host = "localhost";
$db = "schoolsystem";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Connection failed"]));
}

$full_name = $_POST['full_name'] ?? '';
$national_id = $_POST['national_id'] ?? '';
$address = $_POST['address'] ?? '';
$birth_date = $_POST['birth_date'] ?? '';
$email = $_POST['email'] ?? '';
$pass = $_POST['pass'] ?? '';
$role = $_POST['role'] ?? 'student';
$class_grade = $_POST['class_grade'] ?? '';

if (empty($full_name) || empty($national_id) || empty($birth_date) || empty($email) || empty($pass)) {
    echo json_encode(["status" => "error", "message" => "Missing input values"]);
    exit;
}

$stmt = $conn->prepare("INSERT INTO users (full_name, national_id, address, birth_date, email, pass, role, class_grade) 
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

$stmt->bind_param("sssssssi", $full_name, $national_id, $address, $birth_date, $email, $pass, $role, $class_grade);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Student added successfully"]);
} else {
    echo json_encode(["status" => "error", "message" => "Failed to add student"]);
}

$stmt->close();
$conn->close();
?>
