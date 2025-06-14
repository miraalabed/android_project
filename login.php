<?php
$host = "localhost";
$db = "schoolsystem"; 
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode(['status' => 'error', 'message' => 'Connection failed: ' . $conn->connect_error]));
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = $_POST['email'];
    $pass = $_POST['pass'];

    $stmt = $conn->prepare("SELECT role FROM users WHERE email=? AND pass=?");
    $stmt->bind_param("ss", $email, $pass);
    $stmt->execute();

    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();  // هنا تجيب بيانات الصف كـ array
        echo json_encode([
            'status' => 'success',
            'role' => $user['role']
        ]);
    } else {
        echo json_encode(['status' => 'error']);
    }

    $stmt->close();
} else {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method']);
}

$conn->close();
?>
