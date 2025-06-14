<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

// تسجيل البيانات الواردة لأغراض التصحيح
error_log("===== New Request Received =====");
error_log("Request Method: " . $_SERVER['REQUEST_METHOD']);
error_log("Headers: " . print_r(getallheaders(), true));
error_log("POST Data: " . print_r($_POST, true));
$input = file_get_contents("php://input");
error_log("Raw Input: " . $input);

try {
    $pdo = new PDO('mysql:host=localhost;dbname=schoolsystem', 'root', '');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // استقبال البيانات سواء كـ POST عادي أو JSON
    $data = json_decode($input, true);
    if (json_last_error() !== JSON_ERROR_NONE) {
        parse_str($input, $data);
    }
    
    error_log("Parsed Data: " . print_r($data, true));
    
    // استخراج القيم مع التحقق من وجودها
    $teacher_id = isset($data['teacher_id']) ? (int)$data['teacher_id'] : 0;
    $subject_id = isset($data['subject_id']) ? (int)$data['subject_id'] : 0;
    $student_id = isset($data['student_id']) ? (int)$data['student_id'] : 0;
    $exam_name = isset($data['exam_name']) ? trim($data['exam_name']) : '';
    $mark = isset($data['mark']) ? trim($data['mark']) : '';
    
    // التحقق من البيانات المطلوبة
    if ($teacher_id <= 0 || $subject_id <= 0 || $student_id <= 0 || empty($exam_name) || empty($mark)) {
        $missing = [];
        if ($teacher_id <= 0) $missing[] = 'teacher_id';
        if ($subject_id <= 0) $missing[] = 'subject_id';
        if ($student_id <= 0) $missing[] = 'student_id';
        if (empty($exam_name)) $missing[] = 'exam_name';
        if (empty($mark)) $missing[] = 'mark';
        
        error_log("Missing fields: " . implode(', ', $missing));
        
        echo json_encode([
            'success' => false,
            'message' => 'الحقول المطلوبة مفقودة أو غير صالحة',
            'missing_fields' => $missing,
            'received_data' => $data
        ]);
        exit;
    }
    
    // التحقق من أن العلامة رقمية
    if (!is_numeric($mark)) {
        echo json_encode([
            'success' => false,
            'message' => 'يجب أن تكون العلامة رقمية'
        ]);
        exit;
    }
    
    // التحقق من أن المعلم يدرس المادة
    $stmt = $pdo->prepare("SELECT COUNT(*) FROM subjects WHERE subject_id = ? AND teacher_id = ?");
    $stmt->execute([$subject_id, $teacher_id]);
    if ($stmt->fetchColumn() == 0) {
        echo json_encode([
            'success' => false,
            'message' => 'المعلم غير مسجل لهذه المادة'
        ]);
        exit;
    }
    
    // التحقق من أن الطالب مسجل في المادة
    $stmt = $pdo->prepare("SELECT COUNT(*) FROM studentschedule WHERE student_id = ? AND subject_id = ?");
    $stmt->execute([$student_id, $subject_id]);
    if ($stmt->fetchColumn() == 0) {
        echo json_encode([
            'success' => false,
            'message' => 'الطالب غير مسجل في هذه المادة'
        ]);
        exit;
    }
    
    // إدخال العلامة في قاعدة البيانات
    $stmt = $pdo->prepare("INSERT INTO marks (student_id, subject_id, exam_name, mark, date) 
                          VALUES (?, ?, ?, ?, CURDATE())");
    $result = $stmt->execute([$student_id, $subject_id, $exam_name, $mark]);
    
    if ($result) {
        echo json_encode([
            'success' => true,
            'message' => 'تم نشر العلامة بنجاح'
        ]);
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'فشل في نشر العلامة'
        ]);
    }
    
} catch (PDOException $e) {
    error_log("Database Error: " . $e->getMessage());
    echo json_encode([
        'success' => false,
        'message' => 'حدث خطأ في قاعدة البيانات',
        'error_details' => $e->getMessage()
    ]);
}
?>