import React from 'react';
import ReactDOM from 'react-dom/client';
import TaskList from './TaskList';

const App = () => {
    return (
        <div>
            <h1>Task Manager</h1>
            <TaskList />
        </div>
    ); 
};
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);