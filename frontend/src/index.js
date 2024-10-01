import React, { useState } from 'react';
import ReactDOM from 'react-dom/client';
import TaskList from './TaskList';
import TaskAddDialog from './TaskAddDialog';

const App = () => {
    const [showAddTaskDialog, setShowAddTaskDialog] = useState(false);

    const handleAddTask = () => {
        setShowAddTaskDialog(true);
    };

    const handleCloseAddTaskDialog = () => {
        setShowAddTaskDialog(false);
    };

    const refreshTasks = () => {};

    const handleAddTaskToTaskList = async (newTask) => {
        try {
          const response = await fetch('http://localhost:9090/api/tasks', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
              description: newTask.description,
              dueDate: newTask.dueDate,
              status: 'PENDING', 
            }),
          });
    
          if (!response.ok) {
            throw new Error(`Failed to add task: ${response.status}`);
          }
    
          handleCloseAddTaskDialog();
          refreshTasks();
        } catch (error) {
          console.error('Error adding task:', error);
        }
      };

    return (
        <div>
            <h1>Task Manager</h1>
            <button onClick={handleAddTask}>Add New Task</button>

            <TaskList onAddTask={refreshTasks} />
            <TaskAddDialog
                isOpen={showAddTaskDialog}
                onClose={handleCloseAddTaskDialog}
                onAddTask={handleAddTaskToTaskList}
            />
        </div>
    );
};
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);