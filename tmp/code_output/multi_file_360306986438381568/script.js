// 使用IIFE避免全局污染
(function() {
    // DOM元素引用
    const taskInput = document.getElementById('taskInput');
    const addTaskBtn = document.getElementById('addTaskBtn');
    const prioritySelect = document.getElementById('prioritySelect');
    const dueDateInput = document.getElementById('dueDate');
    const taskList = document.getElementById('taskList');
    const emptyState = document.getElementById('emptyState');
    const taskCount = document.getElementById('taskCount');
    const clearCompletedBtn = document.getElementById('clearCompletedBtn');
    const filterButtons = document.querySelectorAll('.filter-btn');
    const sortSelect = document.getElementById('sortSelect');
    const exportBtn = document.getElementById('exportBtn');
    const importBtn = document.getElementById('importBtn');
    const importModal = document.getElementById('importModal');
    const closeModalBtn = document.querySelector('.close-modal');
    const cancelImportBtn = document.getElementById('cancelImportBtn');
    const confirmImportBtn = document.getElementById('confirmImportBtn');
    const importDataTextarea = document.getElementById('importData');
    const notification = document.getElementById('notification');
    const notificationText = document.getElementById('notificationText');
    const savedTasksCount = document.getElementById('savedTasksCount');
    
    // 设置默认日期为明天
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    dueDateInput.valueAsDate = tomorrow;
    
    // 任务数组
    let tasks = [];
    let currentFilter = 'all';
    let currentSort = 'added';
    
    // 初始化应用
    function init() {
        loadTasks();
        renderTasks();
        setupEventListeners();
        updateStats();
    }
    
    // 设置事件监听器
    function setupEventListeners() {
        // 添加任务
        addTaskBtn.addEventListener('click', addTask);
        taskInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') addTask();
        });
        
        // 过滤任务
        filterButtons.forEach(btn => {
            btn.addEventListener('click', function() {
                filterButtons.forEach(b => b.classList.remove('active'));
                this.classList.add('active');
                currentFilter = this.dataset.filter;
                renderTasks();
            });
        });
        
        // 清除已完成任务
        clearCompletedBtn.addEventListener('click', clearCompletedTasks);
        
        // 排序任务
        sortSelect.addEventListener('change', function() {
            currentSort = this.value;
            renderTasks();
        });
        
        // 导出/导入任务
        exportBtn.addEventListener('click', exportTasks);
        importBtn.addEventListener('click', () => importModal.style.display = 'flex');
        
        // 模态框控制
        closeModalBtn.addEventListener('click', () => importModal.style.display = 'none');
        cancelImportBtn.addEventListener('click', () => importModal.style.display = 'none');
        
        // 点击模态框外部关闭
        importModal.addEventListener('click', function(e) {
            if (e.target === this) importModal.style.display = 'none';
        });
        
        // 确认导入
        confirmImportBtn.addEventListener('click', importTasks);
    }
    
    // 添加新任务
    function addTask() {
        const title = taskInput.value.trim();
        if (!title) {
            showNotification('请输入任务内容', 'error');
            taskInput.focus();
            return;
        }
        
        const newTask = {
            id: Date.now(),
            title: title,
            priority: prioritySelect.value,
            dueDate: dueDateInput.value,
            completed: false,
            createdAt: new Date().toISOString()
        };
        
        tasks.push(newTask);
        saveTasks();
        renderTasks();
        updateStats();
        
        // 清空输入框
        taskInput.value = '';
        taskInput.focus();
        
        showNotification('任务添加成功');
    }
    
    // 删除任务
    function deleteTask(id) {
        tasks = tasks.filter(task => task.id !== id);
        saveTasks();
        renderTasks();
        updateStats();
        showNotification('任务已删除');
    }
    
    // 切换任务完成状态
    function toggleTaskCompletion(id) {
        tasks = tasks.map(task => {
            if (task.id === id) {
                return { ...task, completed: !task.completed };
            }
            return task;
        });
        
        saveTasks();
        renderTasks();
        updateStats();
        
        const task = tasks.find(t => t.id === id);
        showNotification(task.completed ? '任务标记为已完成' : '任务标记为未完成');
    }
    
    // 编辑任务
    function editTask(id) {
        const task = tasks.find(t => t.id === id);
        if (!task) return;
        
        const newTitle = prompt('编辑任务:', task.title);
        if (newTitle !== null && newTitle.trim() !== '') {
            task.title = newTitle.trim();
            saveTasks();
            renderTasks();
            showNotification('任务已更新');
        }
    }
    
    // 清除已完成任务
    function clearCompletedTasks() {
        const completedCount = tasks.filter(task => task.completed).length;
        if (completedCount === 0) {
            showNotification('没有已完成的任务', 'info');
            return;
        }
        
        if (confirm(`确定要删除 ${completedCount} 个已完成的任务吗？`)) {
            tasks = tasks.filter(task => !task.completed);
            saveTasks();
            renderTasks();
            updateStats();
            showNotification('已完成任务已清除');
        }
    }
    
    // 过滤任务
    function filterTasks() {
        switch(currentFilter) {
            case 'active':
                return tasks.filter(task => !task.completed);
            case 'completed':
                return tasks.filter(task => task.completed);
            default:
                return tasks;
        }
    }
    
    // 排序任务
    function sortTasks(taskArray) {
        const sortedTasks = [...taskArray];
        
        switch(currentSort) {
            case 'priority':
                const priorityOrder = { high: 3, medium: 2, low: 1 };
                sortedTasks.sort((a, b) => priorityOrder[b.priority] - priorityOrder[a.priority]);
                break;
            case 'dueDate':
                sortedTasks.sort((a, b) => {
                    if (!a.dueDate && !b.dueDate) return 0;
                    if (!a.dueDate) return 1;
                    if (!b.dueDate) return -1;
                    return new Date(a.dueDate) - new Date(b.dueDate);
                });
                break;
            default: // 'added'
                sortedTasks.sort((a, b) => b.id - a.id);
                break;
        }
        
        return sortedTasks;
    }
    
    // 渲染任务列表
    function renderTasks() {
        const filteredTasks = filterTasks();
        const sortedTasks = sortTasks(filteredTasks);
        
        // 清空任务列表
        taskList.innerHTML = '';
        
        // 显示/隐藏空状态
        if (sortedTasks.length === 0) {
            emptyState.style.display = 'block';
        } else {
            emptyState.style.display = 'none';
            
            // 渲染每个任务
            sortedTasks.forEach(task => {
                const taskItem = document.createElement('li');
                taskItem.className = `task-item priority-${task.priority}`;
                taskItem.dataset.id = task.id;
                
                // 格式化日期
                let dueDateText = '无截止日期';
                if (task.dueDate) {
                    const dueDate = new Date(task.dueDate);
                    const today = new Date();
                    today.setHours(0, 0, 0, 0);
                    
                    const timeDiff = dueDate - today;
                    const daysDiff = Math.ceil(timeDiff / (1000 * 60 * 60 * 24));
                    
                    if (daysDiff === 0) {
                        dueDateText = '今天到期';
                    } else if (daysDiff === 1) {
                        dueDateText = '明天到期';
                    } else if (daysDiff < 0) {
                        dueDateText = '已过期';
                    } else {
                        dueDateText = `${daysDiff}天后到期`;
                    }
                }
                
                // 优先级文本
                const priorityText = {
                    high: '高优先级',
                    medium: '中优先级',
                    low: '低优先级'
                }[task.priority];
                
                taskItem.innerHTML = `
                    <input type="checkbox" class="task-checkbox" ${task.completed ? 'checked' : ''}>
                    <div class="task-content">
                        <div class="task-title ${task.completed ? 'completed' : ''}">${task.title}</div>
                        <div class="task-meta-info">
                            <span class="task-priority">${priorityText}</span>
                            <span class="task-due-date"><i class="far fa-calendar"></i> ${dueDateText}</span>
                        </div>
                    </div>
                    <div class="task-actions">
                        <button class="task-action-btn edit-btn" title="编辑任务">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="task-action-btn delete-btn" title="删除任务">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                `;
                
                // 添加事件监听器
                const checkbox = taskItem.querySelector('.task-checkbox');
                const editBtn = taskItem.querySelector('.edit-btn');
                const deleteBtn = taskItem.querySelector('.delete-btn');
                
                checkbox.addEventListener('change', () => toggleTaskCompletion(task.id));
                editBtn.addEventListener('click', () => editTask(task.id));
                deleteBtn.addEventListener('click', () => deleteTask(task.id));
                
                taskList.appendChild(taskItem);
            });
        }
    }
    
    // 更新统计信息
    function updateStats() {
        const totalTasks = tasks.length;
        const completedTasks = tasks.filter(task => task.completed).length;
        const activeTasks = totalTasks - completedTasks;
        
        taskCount.textContent = `${totalTasks} 个任务 (${activeTasks} 个待办, ${completedTasks} 个已完成)`;
        savedTasksCount.textContent = totalTasks;
    }
    
    // 导出任务
    function exportTasks() {
        if (tasks.length === 0) {
            showNotification('没有任务可以导出', 'info');
            return;
        }
        
        const exportData = JSON.stringify(tasks, null, 2);
        const blob = new Blob([exportData], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        
        const a = document.createElement('a');
        a.href = url;
        a.download = `tasks_${new Date().toISOString().slice(0, 10)}.json`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        
        URL.revokeObjectURL(url);
        showNotification('任务已导出');
    }
    
    // 导入任务
    function importTasks() {
        const importData = importDataTextarea.value.trim();
        
        if (!importData) {
            showNotification('请输入要导入的数据', 'error');
            return;
        }
        
        try {
            const importedTasks = JSON.parse(importData);
            
            if (!Array.isArray(importedTasks)) {
                throw new Error('数据格式不正确');
            }
            
            // 验证导入的任务数据
            const validTasks = importedTasks.filter(task => 
                task && 
                typeof task.title === 'string' && 
                task.title.trim() !== ''
            );
            
            if (validTasks.length === 0) {
                throw new Error('没有有效的任务数据');
            }
            
            // 合并任务，避免重复ID
            const existingIds = new Set(tasks.map(task => task.id));
            const newTasks = validTasks.filter(task => !existingIds.has(task.id));
            
            // 为新任务生成ID
            newTasks.forEach(task => {
                if (!task.id || existingIds.has(task.id)) {
                    task.id = Date.now() + Math.floor(Math.random() * 1000);
                }
                if (!task.createdAt) {
                    task.createdAt = new Date().toISOString();
                }
            });
            
            tasks = [...tasks, ...newTasks];
            saveTasks();
            renderTasks();
            updateStats();
            
            importModal.style.display = 'none';
            importDataTextarea.value = '';
            
            showNotification(`成功导入 ${newTasks.length} 个任务`);
        } catch (error) {
            showNotification('导入失败: ' + error.message, 'error');
        }
    }
    
    // 显示通知
    function showNotification(message, type = 'success') {
        notificationText.textContent = message;
        
        // 根据类型设置颜色
        if (type === 'error') {
            notification.style.backgroundColor = '#dc3545';
        } else if (type === 'info') {
            notification.style.backgroundColor = '#17a2b8';
        } else {
            notification.style.backgroundColor = '#28a745';
        }
        
        notification.classList.add('show');
        
        // 3秒后隐藏通知
        setTimeout(() => {
            notification.classList.remove('show');
        }, 3000);
    }
    
    // 保存任务到本地存储
    function saveTasks() {
        localStorage.setItem('taskManagerTasks', JSON.stringify(tasks));
    }
    
    // 从本地存储加载任务
    function loadTasks() {
        const savedTasks = localStorage.getItem('taskManagerTasks');
        if (savedTasks) {
            try {
                tasks = JSON.parse(savedTasks);
            } catch (error) {
                console.error('加载任务失败:', error);
                tasks = [];
            }
        }
    }
    
    // 初始化应用
    init();
})();