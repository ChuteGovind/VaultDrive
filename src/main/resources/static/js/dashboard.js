document.addEventListener('DOMContentLoaded', () => {
    // Current State
    let currentUser = null;
    let currentFilter = 'all'; // 'all', 'IMAGE', 'VIDEO', etc., or 'recent'
    let currentView = 'grid'; // 'grid' or 'list'
    let allFiles = [];

    // DOM Elements
    const userNameEl = document.getElementById('userName');
    const userRoleEl = document.getElementById('userRole');
    const userAvatarEl = document.getElementById('userAvatar');
    const welcomeMessageEl = document.getElementById('welcomeMessage');
    const logoutBtn = document.getElementById('logoutBtn');
    
    // Stats elements
    const storageDetailsEl = document.getElementById('storageDetails');
    const storageProgressEl = document.getElementById('storageProgress');
    const storageFileCountEl = document.getElementById('storageFileCount');
    const statCountPdf = document.getElementById('statCountPdf');
    const statSizePdf = document.getElementById('statSizePdf');
    const statCountImage = document.getElementById('statCountImage');
    const statSizeImage = document.getElementById('statSizeImage');
    const statCountDocument = document.getElementById('statCountDocument');
    const statSizeDocument = document.getElementById('statSizeDocument');
    const statCountMedia = document.getElementById('statCountMedia');
    const statSizeMedia = document.getElementById('statSizeMedia');

    // List view toggles
    const gridViewBtn = document.getElementById('gridViewBtn');
    const listViewBtn = document.getElementById('listViewBtn');
    const filesContainer = document.getElementById('filesContainer');
    const currentCategoryText = document.getElementById('currentCategoryText');
    const emptyState = document.getElementById('emptyState');
    
    // Search elements
    const searchInput = document.getElementById('searchInput');
    const btnFilterToggle = document.getElementById('btnFilterToggle');
    const advancedSearchPanel = document.getElementById('advancedSearchPanel');
    const searchCategory = document.getElementById('searchCategory');
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const btnResetSearch = document.getElementById('btnResetSearch');
    const btnApplySearch = document.getElementById('btnApplySearch');

    // Modals
    const uploadModal = document.getElementById('uploadModal');
    const openUploadModalBtn = document.getElementById('openUploadModalBtn');
    const closeUploadModal = document.getElementById('closeUploadModal');
    const cancelUploadBtn = document.getElementById('cancelUploadBtn');
    const uploadForm = document.getElementById('uploadForm');
    const dropzone = document.getElementById('dropzone');
    const fileInput = document.getElementById('fileInput');
    const selectedFileName = document.getElementById('selectedFileName');
    const selectedFileSize = document.getElementById('selectedFileSize');
    const fileSpecs = document.getElementById('fileSpecs');
    const uploadCategory = document.getElementById('uploadCategory');
    const uploadDescription = document.getElementById('uploadDescription');
    const uploadProgressContainer = document.getElementById('uploadProgressContainer');
    const uploadProgressFill = document.getElementById('uploadProgressFill');
    const uploadProgressText = document.getElementById('uploadProgressText');
    
    // Edit Modal
    const editModal = document.getElementById('editModal');
    const closeEditModal = document.getElementById('closeEditModal');
    const cancelEditBtn = document.getElementById('cancelEditBtn');
    const editForm = document.getElementById('editForm');
    const editFileId = document.getElementById('editFileId');
    const editFileName = document.getElementById('editFileName');
    const editCategory = document.getElementById('editCategory');
    const editDescription = document.getElementById('editDescription');
    
    // Preview Modal
    const previewModal = document.getElementById('previewModal');
    const closePreviewModal = document.getElementById('closePreviewModal');
    const previewTitle = document.getElementById('previewTitle');
    const previewBody = document.getElementById('previewBody');
    const previewDownloadBtn = document.getElementById('previewDownloadBtn');
    const previewDescription = document.getElementById('previewDescription');

    // Details Modal
    const detailsModal = document.getElementById('detailsModal');
    const closeDetailsModal = document.getElementById('closeDetailsModal');
    const closeDetailsBtn = document.getElementById('closeDetailsBtn');
    const detName = document.getElementById('detName');
    const detCategory = document.getElementById('detCategory');
    const detSize = document.getElementById('detSize');
    const detMime = document.getElementById('detMime');
    const detUploaded = document.getElementById('detUploaded');
    const detModified = document.getElementById('detModified');
    const detDesc = document.getElementById('detDesc');

    // Delete Modal
    const deleteModal = document.getElementById('deleteModal');
    const closeDeleteModal = document.getElementById('closeDeleteModal');
    const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
    const deleteFileName = document.getElementById('deleteFileName');
    const deleteFileId = document.getElementById('deleteFileId');

    const alertBox = document.getElementById('alertBox');

    // Alert functions
    const showAlert = (message, isError = false) => {
        alertBox.textContent = message;
        alertBox.className = `alert ${isError ? 'error' : 'success'}`;
        alertBox.classList.remove('hidden');
        setTimeout(() => alertBox.classList.add('hidden'), 5000);
    };

    // 1. Initial State & Authentication Check
    const checkAuth = async () => {
        try {
            const res = await fetch('/api/auth/me');
            if (!res.ok) {
                window.location.href = '/login.html';
                return;
            }
            currentUser = await res.json();
            
            // Render user details
            userNameEl.textContent = currentUser.fullName;
            userRoleEl.textContent = currentUser.role;
            const initials = currentUser.fullName.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
            userAvatarEl.textContent = initials;
            welcomeMessageEl.textContent = `Welcome, ${currentUser.fullName}!`;
            
            // Run page updates
            refreshPageData();
        } catch (err) {
            window.location.href = '/login.html';
        }
    };

    // 2. Fetch Stats & Refresh files
    const refreshPageData = async () => {
        fetchStats();
        fetchFiles();
    };

    const fetchStats = async () => {
        try {
            const res = await fetch('/api/files/stats');
            if (res.ok) {
                const stats = await res.json();
                
                // Storage Usage
                const maxStorage = 500 * 1024 * 1024; // 500MB
                const totalBytes = stats.totalSize || 0;
                const ratio = Math.min((totalBytes / maxStorage) * 100, 100);
                
                storageProgressEl.style.width = `${ratio}%`;
                storageDetailsEl.textContent = `${stats.formattedTotalSize} of 500.00 MB used`;
                storageFileCountEl.textContent = `${stats.totalFiles} files`;

                // Categories
                const cats = stats.categories || {};
                
                // Document category count and size
                const docStats = cats['DOCUMENT'] || { count: 0, size: 0, formattedSize: "0 B" };
                statCountDocument.textContent = `${docStats.count} files`;
                statSizeDocument.textContent = docStats.formattedSize;

                // PDF stats
                const pdfStats = cats['PDF'] || { count: 0, size: 0, formattedSize: "0 B" };
                statCountPdf.textContent = `${pdfStats.count} files`;
                statSizePdf.textContent = pdfStats.formattedSize;

                // Photo stats
                const imageStats = cats['IMAGE'] || { count: 0, size: 0, formattedSize: "0 B" };
                statCountImage.textContent = `${imageStats.count} files`;
                statSizeImage.textContent = imageStats.formattedSize;

                // Media stats (Video & Audio combined)
                const videoStats = cats['VIDEO'] || { count: 0, size: 0 };
                const audioStats = cats['AUDIO'] || { count: 0, size: 0 };
                const mediaCount = (videoStats.count || 0) + (audioStats.count || 0);
                const mediaSize = (videoStats.size || 0) + (audioStats.size || 0);
                
                statCountMedia.textContent = `${mediaCount} files`;
                statSizeMedia.textContent = formatBytes(mediaSize);
            }
        } catch (err) {
            console.error("Error fetching stats:", err);
        }
    };

    const fetchFiles = async () => {
        try {
            let url = '/api/files';
            
            if (currentFilter === 'recent') {
                url = '/api/files/recent';
            } else if (currentFilter !== 'all') {
                url = `/api/files/search?category=${currentFilter}`;
            }

            const res = await fetch(url);
            if (res.ok) {
                allFiles = await res.json();
                renderFiles();
            }
        } catch (err) {
            showAlert("Could not load files.", true);
        }
    };

    // Helper to format bytes
    function formatBytes(bytes) {
        if (bytes === 0) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    // Helper to get FontAwesome icon matching category
    const getCategoryIcon = (category) => {
        switch (category) {
            case 'IMAGE': return 'fa-solid fa-file-image file-row-icon color-blue';
            case 'VIDEO': return 'fa-solid fa-file-video file-row-icon color-purple';
            case 'AUDIO': return 'fa-solid fa-file-audio file-row-icon color-purple';
            case 'PDF': return 'fa-solid fa-file-pdf file-row-icon color-red';
            case 'DOCUMENT': return 'fa-solid fa-file-word file-row-icon color-green';
            case 'SPREADSHEET': return 'fa-solid fa-file-excel file-row-icon color-green';
            case 'PRESENTATION': return 'fa-solid fa-file-powerpoint file-row-icon color-orange';
            case 'ARCHIVE': return 'fa-solid fa-file-zipper file-row-icon color-gold';
            default: return 'fa-solid fa-file file-row-icon';
        }
    };

    // 3. Render Files in grid or list view
    const renderFiles = () => {
        filesContainer.innerHTML = '';
        
        if (allFiles.length === 0) {
            filesContainer.appendChild(emptyState);
            emptyState.style.display = 'block';
            return;
        }
        emptyState.style.display = 'none';

        if (currentView === 'grid') {
            filesContainer.className = "files-container grid-view";
            
            allFiles.forEach(file => {
                const card = document.createElement('div');
                card.className = 'file-card';
                card.dataset.id = file.id;

                // File visual block
                let visualHtml = `<i class="${getCategoryIcon(file.category)}"></i>`;
                if (file.category === 'IMAGE') {
                    visualHtml = `<img src="/api/files/${file.id}/preview" alt="${file.originalFileName}">`;
                }

                card.innerHTML = `
                    <div class="file-options">
                        <button class="options-btn"><i class="fa-solid fa-ellipsis-vertical"></i></button>
                        <div class="options-dropdown hidden">
                            <button class="opt-preview"><i class="fa-solid fa-eye"></i> Preview</button>
                            <a href="/api/files/${file.id}/download" class="opt-download" download><i class="fa-solid fa-download"></i> Download</a>
                            <button class="opt-edit"><i class="fa-solid fa-pen"></i> Rename</button>
                            <button class="opt-details"><i class="fa-solid fa-circle-info"></i> Details</button>
                            <button class="opt-delete delete-option"><i class="fa-solid fa-trash"></i> Delete</button>
                        </div>
                    </div>
                    <div class="file-thumbnail">
                        ${visualHtml}
                    </div>
                    <div class="file-details-box">
                        <span class="file-title" title="${file.originalFileName}">${file.originalFileName}</span>
                        <div class="file-meta">
                            <span>${formatBytes(file.fileSize)}</span>
                            <span>${new Date(file.uploadedAt).toLocaleDateString()}</span>
                        </div>
                    </div>
                `;

                // Add drop menu click events
                const optBtn = card.querySelector('.options-btn');
                const dropMenu = card.querySelector('.options-dropdown');
                optBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    // Close all other open dropdowns first
                    document.querySelectorAll('.options-dropdown').forEach(d => {
                        if (d !== dropMenu) d.classList.add('hidden');
                    });
                    dropMenu.classList.toggle('hidden');
                });

                // Attach action triggers to specific options
                card.querySelector('.opt-preview').addEventListener('click', (e) => { e.stopPropagation(); openPreviewModal(file); dropMenu.classList.add('hidden'); });
                card.querySelector('.opt-edit').addEventListener('click', (e) => { e.stopPropagation(); openEditModal(file); dropMenu.classList.add('hidden'); });
                card.querySelector('.opt-details').addEventListener('click', (e) => { e.stopPropagation(); openDetailsModal(file); dropMenu.classList.add('hidden'); });
                card.querySelector('.opt-delete').addEventListener('click', (e) => { e.stopPropagation(); openDeleteModal(file); dropMenu.classList.add('hidden'); });
                
                // Allow card clicks to trigger previews directly
                card.addEventListener('click', () => {
                    openPreviewModal(file);
                });

                filesContainer.appendChild(card);
            });
        } else {
            // List View
            filesContainer.className = "files-container list-view";
            
            // Header Row
            const listHeader = document.createElement('div');
            listHeader.className = 'file-list-header';
            listHeader.innerHTML = `
                <span>Name</span>
                <span>Category</span>
                <span>Size</span>
                <span>Uploaded At</span>
                <span></span>
            `;
            filesContainer.appendChild(listHeader);

            allFiles.forEach(file => {
                const row = document.createElement('div');
                row.className = 'file-row';
                row.dataset.id = file.id;

                row.innerHTML = `
                    <div class="file-row-name">
                        <i class="${getCategoryIcon(file.category)}"></i>
                        <span class="file-title" title="${file.originalFileName}">${file.originalFileName}</span>
                    </div>
                    <span class="file-row-category">${file.category}</span>
                    <span class="file-row-size">${formatBytes(file.fileSize)}</span>
                    <span class="file-row-date">${new Date(file.uploadedAt).toLocaleString()}</span>
                    <div class="file-options">
                        <button class="options-btn"><i class="fa-solid fa-ellipsis-vertical"></i></button>
                        <div class="options-dropdown hidden" style="right: 15px;">
                            <button class="opt-preview"><i class="fa-solid fa-eye"></i> Preview</button>
                            <a href="/api/files/${file.id}/download" class="opt-download" download><i class="fa-solid fa-download"></i> Download</a>
                            <button class="opt-edit"><i class="fa-solid fa-pen"></i> Rename</button>
                            <button class="opt-details"><i class="fa-solid fa-circle-info"></i> Details</button>
                            <button class="opt-delete delete-option"><i class="fa-solid fa-trash"></i> Delete</button>
                        </div>
                    </div>
                `;

                // Add drop menu click events
                const optBtn = row.querySelector('.options-btn');
                const dropMenu = row.querySelector('.options-dropdown');
                optBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    document.querySelectorAll('.options-dropdown').forEach(d => {
                        if (d !== dropMenu) d.classList.add('hidden');
                    });
                    dropMenu.classList.toggle('hidden');
                });

                row.querySelector('.opt-preview').addEventListener('click', (e) => { e.stopPropagation(); openPreviewModal(file); dropMenu.classList.add('hidden'); });
                row.querySelector('.opt-edit').addEventListener('click', (e) => { e.stopPropagation(); openEditModal(file); dropMenu.classList.add('hidden'); });
                row.querySelector('.opt-details').addEventListener('click', (e) => { e.stopPropagation(); openDetailsModal(file); dropMenu.classList.add('hidden'); });
                row.querySelector('.opt-delete').addEventListener('click', (e) => { e.stopPropagation(); openDeleteModal(file); dropMenu.classList.add('hidden'); });

                row.addEventListener('click', () => {
                    openPreviewModal(file);
                });

                filesContainer.appendChild(row);
            });
        }
    };

    // Close options menus on page body click
    document.addEventListener('click', () => {
        document.querySelectorAll('.options-dropdown').forEach(d => d.classList.add('hidden'));
    });

    // 4. View Toggles (Grid & List)
    gridViewBtn.addEventListener('click', () => {
        currentView = 'grid';
        gridViewBtn.classList.add('active');
        listViewBtn.classList.remove('active');
        renderFiles();
    });

    listViewBtn.addEventListener('click', () => {
        currentView = 'list';
        listViewBtn.classList.add('active');
        gridViewBtn.classList.remove('active');
        renderFiles();
    });

    // 5. Sidebar Navigation filters
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
            item.classList.add('active');
            
            const filterVal = item.dataset.filter;
            currentFilter = filterVal;
            
            // Adjust category text tag
            if (filterVal === 'all') {
                currentCategoryText.textContent = "My Files";
            } else if (filterVal === 'recent') {
                currentCategoryText.textContent = "Recent Files";
            } else {
                currentCategoryText.textContent = filterVal.charAt(0) + filterVal.slice(1).toLowerCase() + "s";
            }
            
            refreshPageData();
        });
    });

    // 6. Basic and advanced searches
    btnFilterToggle.addEventListener('click', (e) => {
        e.stopPropagation();
        advancedSearchPanel.classList.toggle('hidden');
    });

    // Close search dropdown on click away
    advancedSearchPanel.addEventListener('click', (e) => {
        e.stopPropagation();
    });
    document.addEventListener('click', () => {
        advancedSearchPanel.classList.add('hidden');
    });

    const triggerSearch = async () => {
        const queryName = searchInput.value.trim();
        const queryCat = searchCategory.value;
        const queryStart = startDateInput.value;
        const queryEnd = endDateInput.value;

        try {
            // Build parameters url query
            let paramArr = [];
            if (queryName) paramArr.push(`name=${encodeURIComponent(queryName)}`);
            if (queryCat) paramArr.push(`category=${queryCat}`);
            if (queryStart) paramArr.push(`startDate=${queryStart}`);
            if (queryEnd) paramArr.push(`endDate=${queryEnd}`);

            const separator = paramArr.length > 0 ? "?" : "";
            const res = await fetch(`/api/files/search${separator}${paramArr.join('&')}`);
            if (res.ok) {
                allFiles = await res.json();
                currentCategoryText.textContent = "Search Results";
                renderFiles();
            }
        } catch (err) {
            showAlert("Search query failed.", true);
        }
    };

    // Text search triggers instantly on enter or delay timer
    let searchTimeout = null;
    searchInput.addEventListener('input', () => {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            triggerSearch();
        }, 400);
    });

    btnApplySearch.addEventListener('click', () => {
        triggerSearch();
        advancedSearchPanel.classList.add('hidden');
    });

    btnResetSearch.addEventListener('click', () => {
        searchInput.value = '';
        searchCategory.value = '';
        startDateInput.value = '';
        endDateInput.value = '';
        advancedSearchPanel.classList.add('hidden');
        currentFilter = 'all';
        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.querySelector("[data-filter='all']").classList.add('active');
        currentCategoryText.textContent = "My Files";
        refreshPageData();
    });

    // 7. File Upload Modal Trigger Controls
    openUploadModalBtn.addEventListener('click', () => {
        // Reset upload form
        uploadForm.reset();
        fileSpecs.classList.add('hidden');
        uploadProgressContainer.classList.add('hidden');
        submitUploadBtn.disabled = false;
        uploadModal.classList.remove('hidden');
    });

    const closeUploadModalFunc = () => {
        uploadModal.classList.add('hidden');
    };
    closeUploadModal.addEventListener('click', closeUploadModalFunc);
    cancelUploadBtn.addEventListener('click', closeUploadModalFunc);

    // Dropzone inputs
    dropzone.addEventListener('click', () => fileInput.click());
    
    dropzone.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropzone.classList.add('dragover');
    });
    
    dropzone.addEventListener('dragleave', () => {
        dropzone.classList.remove('dragover');
    });
    
    dropzone.addEventListener('drop', (e) => {
        e.preventDefault();
        dropzone.classList.remove('dragover');
        if (e.dataTransfer.files.length > 0) {
            fileInput.files = e.dataTransfer.files;
            handleFileSelectionChanged();
        }
    });

    fileInput.addEventListener('change', () => {
        handleFileSelectionChanged();
    });

    const handleFileSelectionChanged = () => {
        if (fileInput.files.length > 0) {
            const file = fileInput.files[0];
            selectedFileName.textContent = file.name;
            selectedFileSize.textContent = formatBytes(file.size);
            fileSpecs.classList.remove('hidden');
        }
    };

    // Form submission containing XHR indicator
    uploadForm.addEventListener('submit', (e) => {
        e.preventDefault();
        if (fileInput.files.length === 0) return;

        const file = fileInput.files[0];
        const description = uploadDescription.value.trim();
        const categoryOverride = uploadCategory.value;

        const formData = new FormData();
        formData.append('file', file);
        if (description) formData.append('description', description);
        if (categoryOverride) formData.append('category', categoryOverride);

        submitUploadBtn.disabled = true;
        uploadProgressContainer.classList.remove('hidden');
        uploadProgressFill.style.width = '0%';
        uploadProgressText.textContent = "Uploading 0%";

        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/api/files/upload', true);

        // Upload progress listener
        xhr.upload.addEventListener('progress', (e) => {
            if (e.lengthComputable) {
                const percent = Math.round((e.loaded / e.total) * 100);
                uploadProgressFill.style.width = `${percent}%`;
                uploadProgressText.textContent = `Uploading ${percent}%`;
            }
        });

        // Completion listener
        xhr.onload = function() {
            if (xhr.status === 201 || xhr.status === 200) {
                showAlert("File uploaded successfully.");
                closeUploadModalFunc();
                refreshPageData();
            } else {
                let errMsg = "Upload failed.";
                try {
                    const errRes = JSON.parse(xhr.responseText);
                    errMsg = errRes.message || errMsg;
                } catch(e) {}
                showAlert(errMsg, true);
                submitUploadBtn.disabled = false;
                uploadProgressContainer.classList.add('hidden');
            }
        };

        xhr.onerror = function() {
            showAlert("Upload failed due to connection failure.", true);
            submitUploadBtn.disabled = false;
            uploadProgressContainer.classList.add('hidden');
        };

        xhr.send(formData);
    });

    // 8. Edit Metadata Dialog Controls
    const openEditModal = (file) => {
        editFileId.value = file.id;
        editFileName.value = file.originalFileName;
        editCategory.value = file.category;
        editDescription.value = file.description || '';
        editModal.classList.remove('hidden');
    };

    const closeEditModalFunc = () => {
        editModal.classList.add('hidden');
    };
    closeEditModal.addEventListener('click', closeEditModalFunc);
    cancelEditBtn.addEventListener('click', closeEditModalFunc);

    editForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = editFileId.value;
        const originalFileName = editFileName.value.trim();
        const category = editCategory.value;
        const description = editDescription.value.trim();

        try {
            const res = await fetch(`/api/files/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ originalFileName, category, description })
            });

            if (res.ok) {
                showAlert("File information updated.");
                closeEditModalFunc();
                refreshPageData();
            } else {
                const data = await res.json();
                showAlert(data.message || "Failed to update info.", true);
            }
        } catch (err) {
            showAlert("Update failed due to network error.", true);
        }
    });

    // 9. Details Dialog Controls
    const openDetailsModal = (file) => {
        detName.textContent = file.originalFileName;
        detCategory.textContent = file.category;
        detSize.textContent = `${formatBytes(file.fileSize)} (${file.fileSize} bytes)`;
        detMime.textContent = file.mimeType;
        detUploaded.textContent = new Date(file.uploadedAt).toLocaleString();
        detModified.textContent = new Date(file.updatedAt).toLocaleString();
        detDesc.textContent = file.description || 'No description provided';
        detailsModal.classList.remove('hidden');
    };
    const closeDetailsModalFunc = () => detailsModal.classList.add('hidden');
    closeDetailsModal.addEventListener('click', closeDetailsModalFunc);
    closeDetailsBtn.addEventListener('click', closeDetailsModalFunc);

    // 10. Delete Dialog Controls
    const openDeleteModal = (file) => {
        deleteFileId.value = file.id;
        deleteFileName.textContent = file.originalFileName;
        deleteModal.classList.remove('hidden');
    };
    const closeDeleteModalFunc = () => deleteModal.classList.add('hidden');
    closeDeleteModal.addEventListener('click', closeDeleteModalFunc);
    cancelDeleteBtn.addEventListener('click', closeDeleteModalFunc);

    confirmDeleteBtn.addEventListener('click', async () => {
        const id = deleteFileId.value;
        try {
            const res = await fetch(`/api/files/${id}`, { method: 'DELETE' });
            if (res.ok) {
                showAlert("File deleted successfully.");
                closeDeleteModalFunc();
                refreshPageData();
            } else {
                const data = await res.json();
                showAlert(data.message || "Failed to delete file.", true);
            }
        } catch (err) {
            showAlert("Network error during file deletion.", true);
        }
    });

    // 11. Custom Previews Modal
    const openPreviewModal = (file) => {
        previewTitle.textContent = file.originalFileName;
        previewDownloadBtn.onclick = () => window.location.href = `/api/files/${file.id}/download`;
        previewDescription.textContent = file.description ? `Description: ${file.description}` : 'No description provided.';
        
        previewBody.innerHTML = '';
        
        const cat = file.category;
        const mime = file.mimeType.toLowerCase();

        if (cat === 'IMAGE') {
            const img = document.createElement('img');
            img.src = `/api/files/${file.id}/preview`;
            img.alt = file.originalFileName;
            previewBody.appendChild(img);
        } else if (cat === 'VIDEO') {
            const video = document.createElement('video');
            video.src = `/api/files/${file.id}/preview`;
            video.controls = true;
            video.autoplay = true;
            previewBody.appendChild(video);
        } else if (cat === 'AUDIO') {
            const audio = document.createElement('audio');
            audio.src = `/api/files/${file.id}/preview`;
            audio.controls = true;
            audio.autoplay = true;
            previewBody.appendChild(audio);
        } else if (cat === 'PDF') {
            const iframe = document.createElement('iframe');
            iframe.src = `/api/files/${file.id}/preview`;
            previewBody.appendChild(iframe);
        } else if (mime.startsWith('text/') || file.extension === 'txt' || file.extension === 'log' || file.extension === 'json') {
            // Fetch preview content text
            const pre = document.createElement('pre');
            pre.textContent = "Loading preview...";
            previewBody.appendChild(pre);

            fetch(`/api/files/${file.id}/preview`)
                .then(res => {
                    if (res.ok) return res.text();
                    return "Could not load preview text.";
                })
                .then(text => {
                    pre.textContent = text;
                })
                .catch(() => {
                    pre.textContent = "Connection error while loading preview.";
                });
        } else {
            // Fallback for unsupported types
            const container = document.createElement('div');
            container.className = 'empty-state';
            container.style.color = '#f8fafc';
            container.innerHTML = `
                <i class="${getCategoryIcon(file.category)} animate-bounce" style="font-size:72px; margin-bottom:20px; opacity:0.8;"></i>
                <h3 style="color:#ffffff; font-size:18px;">Preview Unavailable</h3>
                <p style="color:#94a3b8; margin: 10px 0 20px 0;">This file type cannot be previewed directly in the browser.</p>
                <a href="/api/files/${file.id}/download" class="btn-submit" style="display:inline-block; text-decoration:none; padding:10px 24px; border-radius:8px;" download>
                    <i class="fa-solid fa-download"></i> Download File
                </a>
            `;
            previewBody.appendChild(container);
        }

        previewModal.classList.remove('hidden');
    };

    const closePreviewModalFunc = () => {
        // Pause any running audio/video elements upon close
        const activeMedia = previewBody.querySelector('video, audio');
        if (activeMedia) {
            activeMedia.pause();
            activeMedia.src = "";
        }
        previewModal.classList.add('hidden');
    };
    closePreviewModal.addEventListener('click', closePreviewModalFunc);

    // 12. Logout Request Handler
    logoutBtn.addEventListener('click', async () => {
        try {
            const res = await fetch('/api/auth/logout', { method: 'POST' });
            if (res.ok) {
                window.location.href = '/login.html';
            }
        } catch (err) {
            // redirect to login anyway as fallback
            window.location.href = '/login.html';
        }
    });

    // Run Auth Verification
    checkAuth();
});
