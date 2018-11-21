export default {
  column: {
    number: 'Number',
    form: 'Demand type',
    stage: 'Current stage',
    employee: 'Employee',
    created_at: 'Created at',
    created_by: 'Created by',
    changed_at: 'Changed at',
    changed_by: 'Changed by'
  },
  field: {
    employee: 'Employee',
    personnumber: 'Personnel number',
    position: 'Position',
    organization: 'Organization',
    department: 'Department',
    manager: 'Manager',
    email: 'Email',
    phone: 'Phone',
    location: 'Location',
    comment: 'Comment',
    rating: 'Rating',
    request: 'Request',
    recipient: 'Recipient',
    files: 'Files',
    tip: {
      type_name: 'Start type the name',
      choose_employee: 'First choose an employee',
      choose_location: 'Put your actual location',
      choose_option: 'Choose an option',
      comment: 'Extra information',
      request: 'Put your request text'
    }
  },
  mode: {
    author: 'Author',
    approver: 'Approver',
    executor: 'Executor'
  },
  label: {
    title: 'Demands Management',
    new_request: 'New demand',
    file_deletion: 'Delete a file',
    filters: 'Filters',
    reset: 'Reset',
    send: 'Send',
    role: 'Role',
    demand_title: 'Demand {id} at {date}',
    demand_title2: 'Demand {id}',
    demand_title3: 'Demand',
    stages_title: 'Stages of Demand {id}',
    rating_title: 'Rate the service quality',
    actions: 'Actions',
    from: 'from',
    to: 'to',
    yes: 'Yes',
    no: 'No'
  },
  action: {
    DO_PRINT: 'Print',
    DO_UPLOAD: 'Upload files',
    DO_DOWNLOAD: 'Download file',
    DO_RATE: 'Rate',
    STEP_CREATE: 'Create',
    STEP_CONFIRM: 'Confirm',
    STEP_ACCEPT: 'Accept',
    STEP_REACCEPT: 'Reaccept',
    STEP_PROCESS: 'Process',
    STEP_ASKAPPROVE: 'Ask approval',
    STEP_APPROVE: 'Approve',
    STEP_FINISH: 'Finish',
    STEP_CANCEL: 'Cancel',
    STEP_ASKREFUSE: 'Ask refusal',
    STEP_REFUSE: 'Refuse'
  },
  stage: {
    STEP_CREATE: 'Pending confirmation',
    STEP_CONFIRM: 'Given to executor',
    STEP_ACCEPT: 'Accepted by executor',
    STEP_REACCEPT: 'Executor has been changed',
    STEP_PROCESS: 'Processing',
    STEP_ASKAPPROVE: 'Pending approval',
    STEP_APPROVE: 'Approved to execution',
    STEP_FINISH: 'Finished',
    STEP_CANCEL: 'Cancelled by author',
    STEP_ASKREFUSE: 'Pending refusal',
    STEP_REFUSE: 'Refused'
  },
  unknown_type: 'Unknowm type',
  dictionary_error: 'Dictionary loading error',
  saving_error: 'Data saving error',
  refreshed: 'Data have been refreshed',
  loading_error: 'Data loading error',
  confirm: 'Confirm action',
  confirm_form_cancel: 'Are you sure to cancel without saving form data?',
  form_cancel: 'Form process has been cancelled',
  continue: 'Continued...',
  cancelled: 'Action has been cancelled',
  employee_code_required: 'Personnel number is required',
  employee_code_invalid: 'Invalid personnel number',
  employee_email_required: 'Email address is required',
  employee_email_invalid: 'Invalid email address',
  chief_required: 'Manager is required',
  chief_invalid: 'Manager account ID is undefined',
  organization_required: 'Organization is required',
  department_required: 'Department is required',
  comment_length: 'Comment is too long (max length {max} characters)',
  form_invalid: 'Form contains errors',
  form_undefined: 'Choose demand type and fill required fields',
  form_empty: 'Demand is not filled',
  form_sent: 'Demand has been sent',
  employee_not_found: 'Employee is not found',
  choose_organization: 'First choose an organization',
  add_message: 'Add a message',
  textarea_length: 'Text is too long (max length {max} characters)',
  rating_sent: 'Rating has been sent',
  file_sent: 'File has been uploaded: {name}',
  upload_error: 'File uploading error {name}',
  download_error: 'File downloading error {name}',
  confirm_file_deletion: 'Are you sure to delete the attached file?',
  file_deleted: 'File has been deleted: {name}',
  file_deletion_error: 'File deleting error {name}',
  unknown_file: 'Unknown file',
  undefined_error: 'Unknown error, see F12 Console',
  initialization_error: 'Initialization error',
  identification_error: 'Identification error',
  configuration_error: 'Configuration loading error',
  error_404: 'Not found',
  error_413: 'The file is too large'
}
