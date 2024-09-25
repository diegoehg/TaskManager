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

    const handleAddTaskToTaskList = (newTask) => {
        console.log('Adding task:', newTask);
        setShowAddTaskDialog(false);
    };

    return (
        <div>
            <h1>Task Manager</h1>
            <button onClick={handleAddTask}>Add New Task</button>

            <TaskList />
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