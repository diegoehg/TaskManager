import React, { useState } from 'react';

const TaskAddDialog = ({ isOpen, onClose, onAddTask }) => {
  const [taskDescription, setTaskDescription] = useState('');
  const [dueDate, setDueDate] = useState(new Date().toISOString().slice(0, 10));

  const handleDescriptionChange = (event) => {
    setTaskDescription(event.target.value.slice(0, 400));
  };

  const handleDueDateChange = (event) => {
    setDueDate(event.target.value);
  };

  const handleSubmit = () => {
    onAddTask({ description: taskDescription, dueDate });
    setTaskDescription('');
    setDueDate(new Date().toISOString().slice(0, 10));
  };

  if (!isOpen) {
    return null;
  }

  return (
    <div className="modal">
      <div className="modal-content">
        <span className="close" onClick={onClose}>
          &times;
        </span>
        <h2>Add New Task</h2>
        <label htmlFor="description">Description:</label>
        <input
          type="text"
          id="description"
          value={taskDescription}
          onChange={handleDescriptionChange}
          maxLength={400}
        />

        <label htmlFor="dueDate">Due Date:</label>
        <input
          type="date"
          id="dueDate"
          value={dueDate}
          onChange={handleDueDateChange}
        />

        <button onClick={handleSubmit}>Add Task</button>
      </div>
    </div>
  );
};

export default TaskAddDialog;
