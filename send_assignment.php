<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

require_once 'db_config.php';

// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Function to send JSON response
function send_response($data, $status_code = 200) {
    http_response_code($status_code);
    echo json_encode($data);
    exit;
}

try {
    // Get input data
    $input = file_get_contents("php://input");
    $data = json_decode($input, true);
    
    // If JSON decode fails, try regular POST
    if (json_last_error() !== JSON_ERROR_NONE) {
        $data = $_POST;
    }
    
    // Log received data for debugging
    error_log("Received data: " . print_r($data, true));
    
    // Validate required fields
    $required_fields = ['class_id', 'title', 'due_date'];
    $missing_fields = [];
    
    foreach ($required_fields as $field) {
        if (!isset($data[$field]) || empty(trim($data[$field]))) {
            $missing_fields[] = $field;
        }
    }
    
    if (!empty($missing_fields)) {
        send_response([
            'success' => false,
            'message' => 'Missing required fields: ' . implode(', ', $missing_fields)
        ], 400);
    }
    
    // Get teacher ID from session or token (temporary using static value)
    $teacher_id = 2; // Replace with actual teacher ID from session
    
    // Prepare data
    $subject_id = intval($data['class_id']);
    $title = trim($data['title']);
    $description = isset($data['description']) ? trim($data['description']) : null;
    $due_date = trim($data['due_date']);
    
    // Validate date format (YYYY-MM-DD)
    if (!preg_match("/^\d{4}-\d{2}-\d{2}$/", $due_date)) {
        send_response([
            'success' => false,
            'message' => 'Invalid date format. Use YYYY-MM-DD'
        ], 400);
    }
    
    // Insert into database
    $stmt = $link->prepare("INSERT INTO assignments (subject_id, teacher_id, title, description, due_date) 
                           VALUES (?, ?, ?, ?, ?)");
    
    if (!$stmt) {
        throw new Exception("Database prepare error: " . $link->error);
    }
    
    $stmt->bind_param("iisss", $subject_id, $teacher_id, $title, $description, $due_date);
    
    if ($stmt->execute()) {
        send_response([
            'success' => true,
            'message' => 'Assignment created successfully',
            'assignment_id' => $stmt->insert_id
        ], 201);
    } else {
        throw new Exception("Database execute error: " . $stmt->error);
    }
    
} catch (Exception $e) {
    error_log("Error: " . $e->getMessage());
    send_response([
        'success' => false,
        'message' => 'Server error: ' . $e->getMessage()
    ], 500);
}
?>