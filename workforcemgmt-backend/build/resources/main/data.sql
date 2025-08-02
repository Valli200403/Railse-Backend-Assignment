INSERT INTO task (title, description, assignee, start_date, due_date, priority, cancelled, creation_date, last_modified_date) VALUES
('Implement Login', 'Develop user authentication module', 'Alice', '2025-07-25 10:00:00', '2025-08-05 10:00:00', 'HIGH', false, NOW(), NOW()),
('Design Database Schema', 'Create ER diagrams for the task management system', 'Bob', '2025-07-20 09:00:00', '2025-08-01 14:30:00', 'URGENT', false, NOW(), NOW()),
('Write API Documentation', 'Document all REST endpoints using Swagger/OpenAPI', 'Alice', '2025-08-01 11:00:00', '2025-08-10 16:00:00', 'MEDIUM', false, NOW(), NOW()),
('Fix UI Bug', 'Resolve display issue on task list page', 'Charlie', '2025-07-25 09:00:00', '2025-07-28 09:00:00', 'LOW', true, NOW(), NOW());
