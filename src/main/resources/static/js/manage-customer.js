// manage-customer.js

document.addEventListener("DOMContentLoaded", () => {
    loadCustomers();

    // Bắt sự kiện tìm kiếm
    document.getElementById("btnSearch").addEventListener("click", () => {
        const keyword = document.getElementById("txtSearch").value;
        loadCustomers(keyword);
    });

    // Tìm kiếm khi nhấn Enter
    document.getElementById("txtSearch").addEventListener("keypress", (e) => {
        if (e.key === 'Enter') {
            loadCustomers(e.target.value);
        }
    });
});

async function loadCustomers(keyword = "") {
    try {
        // Gọi API Backend
        const url = keyword
            ? `${API_BASE_URL}/customers?search=${encodeURIComponent(keyword)}`
            : `${API_BASE_URL}/customers`;

        const res = await fetch(url);
        if (!res.ok) throw new Error("Lỗi tải dữ liệu");

        const data = await res.json();
        renderTable(data);
    } catch (err) {
        console.error("Error:", err);
        alert("Không thể tải danh sách khách hàng. Vui lòng kiểm tra Server!");
    }
}

function renderTable(customers) {
    const tbody = document.getElementById("tbodyCustomer");
    tbody.innerHTML = "";

    if (customers.length === 0) {
        tbody.innerHTML = "<tr><td colspan='6' style='text-align:center;'>Không tìm thấy dữ liệu</td></tr>";
        return;
    }

    customers.forEach(c => {
        const row = `
            <tr>
                <td>${c.id}</td>
                <td><strong>${c.fullName}</strong></td>
                <td>${c.phone}</td>
                <td>${c.email || '-'}</td>
                <td><span style="color: #28a745; font-weight: bold;">${c.loyaltyPoints}</span></td>
                <td>
                    <a href="editCustomer.html?id=${c.id}" class="btn btn-sm btn-primary">Sửa</a>
                    <button class="btn btn-sm btn-danger" onclick="deleteCustomer(${c.id})">Xóa</button>
                </td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}

async function deleteCustomer(id) {
    if(!confirm("Bạn có chắc chắn muốn xóa khách hàng này không? Hành động này không thể hoàn tác.")) return;

    try {
        const res = await fetch(`${API_BASE_URL}/customers/${id}`, { method: "DELETE" });
        if (res.ok) {
            alert("Đã xóa thành công!");
            loadCustomers(); // Tải lại danh sách
        } else {
            alert("Xóa thất bại!");
        }
    } catch (err) {
        console.error(err);
        alert("Lỗi kết nối Server!");
    }
}