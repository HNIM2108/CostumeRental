document.addEventListener("DOMContentLoaded", () => {
    // Set mặc định ngày (Đầu tháng -> Hiện tại)
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    
    document.getElementById("dtpStartDate").valueAsDate = firstDay;
    document.getElementById("dtpEndDate").valueAsDate = today;

    document.getElementById("btnViewReport").onclick = loadReport;
});

async function loadReport() {
    const type = document.getElementById("cboReportType").value;
    const start = document.getElementById("dtpStartDate").value;
    const end = document.getElementById("dtpEndDate").value;

    if (!start || !end) { alert("Vui lòng chọn ngày!"); return; }

    // Hiển thị tiêu đề
    const typeText = (type === "CASH_FLOW") ? "Dòng tiền thực thu" : "Doanh số bán hàng";
    document.getElementById("lblReportTitle").innerText = `Báo cáo ${typeText} (${start} đến ${end})`;
    document.getElementById("section-result").style.display = "block";

    try {
        const res = await fetch(`${API_BASE_URL}/statistics/revenue?type=${type}&start=${start}&end=${end}`);
        
        if (!res.ok) throw new Error(await res.text());
        
        const data = await res.json();
        renderTable(data);
        updateSummary(data);
        
    } catch (err) {
        console.error(err);
        alert("Lỗi: " + err.message);
    }
}

function renderTable(data) {
    const tbody = document.getElementById("tbodyResult");
    tbody.innerHTML = "";

    if (data.length === 0) {
        tbody.innerHTML = "<tr><td colspan='5' style='text-align:center'>Không có dữ liệu</td></tr>";
        return;
    }

    data.forEach((item, index) => {
        const row = `
            <tr>
                <td>${index + 1}</td>
                <td>${item.customerId}</td>
                <td>${item.customerName}</td>
                <td>${item.transactionCount}</td>
                <td style="font-weight: bold; color: #28a745;">${formatCurrency(item.totalRevenue)}</td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}

function updateSummary(data) {
    let totalRevenue = 0;
    let totalCount = 0;

    data.forEach(item => {
        totalRevenue += item.totalRevenue;
        totalCount += item.transactionCount;
    });

    document.getElementById("lblTotalRevenue").innerText = formatCurrency(totalRevenue);
    document.getElementById("lblOrderCount").innerText = totalCount;
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}