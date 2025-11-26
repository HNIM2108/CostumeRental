// Biến toàn cục lưu ID khách hàng đang thao tác
let currentCustomerId = null;
let currentCartTotal = 0;       // Tổng tiền hàng (Gốc)
let currentDiscountPercent = 0;

document.addEventListener("DOMContentLoaded", () => {
    loadCostumes();

    // 1. Sự kiện Tìm Khách Hàng
    document.getElementById("btnFindCustomer").onclick = async () => {
        const phone = document.getElementById("txtCustomerPhone").value.trim();
        if (!phone) { alert("Vui lòng nhập số điện thoại!"); return; }

        try {
            const res = await fetch(`${API_BASE_URL}/customers?search=${phone}`);
            const data = await res.json();

            if (data && data.length > 0) {
                const customer = data[0];

                // Cập nhật giao diện
                currentCustomerId = customer.id;
                document.getElementById("lblCustomerName").innerText = customer.fullName;
                document.getElementById("lblLoyaltyPoints").innerText = customer.loyaltyPoints;
                document.getElementById("hidCustomerId").value = customer.id;

                document.getElementById("customerInfoArea").style.display = "block";

                // --- SỬA: Tải giỏ hàng NGAY LẬP TỨC ---
                await loadCart();

                // Thông báo nhỏ (hoặc bỏ đi để trải nghiệm mượt hơn)
                // alert(`Đã chọn khách: ${customer.fullName}`);
            } else {
                alert("Không tìm thấy khách hàng! Vui lòng tạo mới.");
                currentCustomerId = null;
                document.getElementById("customerInfoArea").style.display = "none";
                document.getElementById("tbodyCart").innerHTML = ""; // Xóa giỏ cũ nếu không tìm thấy khách
            }
        } catch (err) {
            console.error(err);
            alert("Lỗi khi tìm khách hàng!");
        }
    };

    // 2. Sự kiện Tìm Trang phục
    document.getElementById("btnSearchCostume").onclick = () => {
        const keyword = document.getElementById("txtSearchCostume").value;
        loadCostumes(keyword);
    };

    // 3. Sự kiện Thêm vào giỏ
    document.getElementById("btnAddToCart").onclick = addToCart;

    // 4. Sự kiện Xác nhận đơn
    document.getElementById("btnConfirmBooking").onclick = confirmBooking;

    document.getElementById("btnApplyPromo").onclick = checkPromotion;
});

function updatePricingUI() {
    // 1. Hiển thị Tổng tiền hàng
    document.getElementById("lblTotalAmount").innerText = formatCurrency(currentCartTotal);

    // 2. Tính tiền giảm giá
    let discountAmount = 0;
    if (currentDiscountPercent > 0) {
        discountAmount = currentCartTotal * (currentDiscountPercent / 100);
        document.getElementById("lblDiscountAmount").innerText = `-${formatCurrency(discountAmount)}`;
        document.getElementById("lblDiscountAmount").style.color = "green";
    } else {
        document.getElementById("lblDiscountAmount").innerText = "- 0 đ";
        document.getElementById("lblDiscountAmount").style.color = "black";
    }

    // 3. Tính tổng sau giảm
    const finalTotal = currentCartTotal - discountAmount;
    document.getElementById("lblAfterDiscount").innerText = formatCurrency(finalTotal);

    // 4. Tính tiền cọc (30% của tổng sau giảm)
    const deposit = finalTotal * 0.3;
    document.getElementById("lblDepositAmount").innerText = formatCurrency(deposit);
    document.getElementById("lblFinalTotal").innerText = formatCurrency(deposit);
}

async function loadCostumes(keyword = "") {
    let url = keyword ? `${API_BASE_URL}/costumes?search=${keyword}` : `${API_BASE_URL}/costumes`;
    try {
        const res = await fetch(url);
        const costumes = await res.json();

        const tbody = document.getElementById("tbodyCostumeList");
        tbody.innerHTML = "";



        costumes.forEach(c => {
            tbody.innerHTML += `
                <tr>
                    <td>${c.id}</td>
                    <td>${c.name}</td>
                    <td>${c.size}</td>
                    <td>${formatCurrency(c.rentalPrice)}</td>
                    <td>${c.quantityAvailable}</td>
                    <td>
                        <button class="btn btn-sm btn-primary"
                            onclick="selectCostume(${c.id}, '${c.name}', ${c.rentalPrice})">
                            Chọn
                        </button>
                    </td>
                </tr>
            `;
        });
    } catch (err) { console.error(err); }
}

function selectCostume(id, name, price) {
    document.getElementById("txtSelectedCostumeId").value = id;
    document.getElementById("txtSelectedCostumePrice").value = price;
    document.getElementById("lblSelectedCostumeName").innerText = name;
    document.getElementById("btnAddToCart").disabled = false;
}

async function addToCart() {
    if (!currentCustomerId) {
        alert("Vui lòng TÌM và CHỌN khách hàng trước!");
        return;
    }
    const costumeId = document.getElementById("txtSelectedCostumeId").value;
    const quantity = document.getElementById("txtQuantity").value;

    if (!costumeId) return;

    try {
        const url = `${API_BASE_URL}/cart/add?customerId=${currentCustomerId}&costumeId=${costumeId}&quantity=${quantity}`;
        const res = await fetch(url, { method: "POST" });

        if (res.ok) {
            // --- SỬA: Tải lại toàn bộ giỏ từ Server để đảm bảo đúng dữ liệu ---
            await loadCart();
            alert("Đã thêm vào giỏ!");
        } else {
            const errorMsg = await res.text();
            alert(errorMsg);
        }
    } catch (err) { console.error(err); }
}

// Hàm tải và hiển thị giỏ hàng
async function loadCart() {
    if (!currentCustomerId) return;

    try {
        const res = await fetch(`${API_BASE_URL}/cart?customerId=${currentCustomerId}`);

        if (!res.ok) {
            console.error("Lỗi tải giỏ hàng:", res.status);
            return;
        }

        const items = await res.json();

        const tbody = document.getElementById("tbodyCart");
        tbody.innerHTML = "";
        let totalAmount = 0;
        currentCartTotal = 0;

        if (!items || items.length === 0) {
            tbody.innerHTML = "<tr><td colspan='6' style='text-align:center'>Giỏ hàng trống</td></tr>";
        } else {
            items.forEach((item, index) => {
                const lineTotal = item.costume.rentalPrice * item.quantity;
                totalAmount += lineTotal;
                currentCartTotal += lineTotal;

                tbody.innerHTML += `
                    <tr>
                        <td>${index + 1}</td>
                        <td>${item.costume.name}</td>
                        <td>${formatCurrency(item.costume.rentalPrice)}</td>
                        <td>${item.quantity}</td>
                        <td>${formatCurrency(lineTotal)}</td>
                        <td><button class="btn btn-danger btn-sm" onclick="deleteCartItem(${item.id})">Xóa</button></td>
                    </tr>
                `;
            });
        }
        updateSummary(totalAmount);
    } catch (err) { console.error(err); }
}

function updateSummary(total) {
    // 1. Tiền hàng tạm tính
    document.getElementById("lblTotalAmount").innerText = formatCurrency(total);

    // 2. Mặc định khi chưa áp mã: Giảm giá = 0
    document.getElementById("lblDiscountAmount").innerText = "- 0 đ";
    document.getElementById("lblDiscountAmount").style.color = "black";
    document.getElementById("lblPromoMessage").innerText = ""; // Xóa thông báo cũ

    // 3. Tổng tiền thuê (Sau giảm) = Chính là tổng tiền gốc
    document.getElementById("lblAfterDiscount").innerText = formatCurrency(total);

    // 4. Tiền cọc (30% của Tổng tiền thuê)
    const deposit = total * 0.3;
    document.getElementById("lblDepositAmount").innerText = formatCurrency(deposit);

    // 5. Tổng cần trả ngay (Chính là tiền cọc)
    document.getElementById("lblFinalTotal").innerText = formatCurrency(deposit);
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

function parseCurrency(moneyText) {
    if (!moneyText) return 0;
    return parseFloat(moneyText.replace(/[^0-9]/g, ''));
}

async function confirmBooking() {
    if (!currentCustomerId) { alert("Chưa chọn khách hàng!"); return; }

    const rentalDate = document.getElementById("dtpStartDate").value;
    const returnDate = document.getElementById("dtpEndDate").value;

    if (!rentalDate || !returnDate) { alert("Vui lòng chọn ngày thuê và ngày trả!"); return; }

    const requestData = {
        customerId: currentCustomerId,
        rentalDate: rentalDate,
        returnDate: returnDate
    };

    if (!confirm("Xác nhận tạo đơn hàng?")) return;

    try {
        const res = await fetch(`${API_BASE_URL}/booking/confirm`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestData)
        });

        if (res.ok) {
            const booking = await res.json();
            alert(`Đặt hàng thành công! Mã đơn: ${booking.id}`);
            location.reload();
        } else {
            alert("Lỗi đặt hàng: " + await res.text());
        }
    } catch (err) { console.error(err); }
}

async function deleteCartItem(cartItemId) {
    if (!confirm("Bạn muốn xóa món này khỏi giỏ?")) return;

    try {
        const res = await fetch(`${API_BASE_URL}/cart/item/${cartItemId}`, {
            method: "DELETE"
        });

        if (res.ok) {
            // Xóa xong thì tải lại bảng
            await loadCart();
        } else {
            alert("Lỗi khi xóa: " + await res.text());
        }
    } catch (err) {
        console.error(err);
        alert("Lỗi kết nối server!");
    }
}

async function checkPromotion() {
    console.log("--- BẮT ĐẦU CHECK PROMOTION ---");
    const code = document.getElementById("txtPromoCode").value.trim();
    const rentalDate = document.getElementById("dtpStartDate").value;

    // Validate
    if (!code) { alert("Vui lòng nhập mã!"); return; }
    if (!rentalDate) { alert("Vui lòng chọn Ngày bắt đầu thuê!"); return; }

    // Kiểm tra biến toàn cục
    console.log("currentCartTotal:", currentCartTotal);
    if (!currentCartTotal || currentCartTotal <= 0) {
        alert("Giỏ hàng đang trống!");
        return;
    }

    try {
        const url = `${API_BASE_URL}/promotions/check?code=${code}&totalAmount=${currentCartTotal}&rentalDate=${rentalDate}`;
        const res = await fetch(url);

        if (res.ok) {
            const discountPercent = await res.json();
            console.log("API Trả về %:", discountPercent);

            // Tính toán
            const discountAmount = currentCartTotal * (discountPercent / 100);
            const finalTotal = currentCartTotal - discountAmount;
            const deposit = finalTotal * 0.3;

            // --- DEBUG TỪNG DÒNG GIAO DIỆN ---
            // Nếu dòng nào báo lỗi đỏ trong Console, nghĩa là HTML thiếu ID đó

            console.log("Update lblDiscountAmount...");
            document.getElementById("lblDiscountAmount").innerText = `-${formatCurrency(discountAmount)}`;
            document.getElementById("lblDiscountAmount").style.color = "green";

            console.log("Update lblAfterDiscount...");
            document.getElementById("lblAfterDiscount").innerText = formatCurrency(finalTotal);

            console.log("Update lblDepositAmount...");
            document.getElementById("lblDepositAmount").innerText = formatCurrency(deposit);

            console.log("Update lblFinalTotal..."); // <--- KHẢ NĂNG CAO LỖI Ở ĐÂY
            const lblFinal = document.getElementById("lblFinalTotal");
            if (lblFinal) {
                lblFinal.innerText = formatCurrency(deposit);
            } else {
                console.warn("CẢNH BÁO: Không tìm thấy ID 'lblFinalTotal' trong HTML!");
            }

            // Thông báo
            const msgSpan = document.getElementById("lblPromoMessage");
            if (msgSpan) {
                msgSpan.innerText = `Đã áp dụng mã: Giảm ${discountPercent}%`;
                msgSpan.style.color = "green";
            }

            console.log("--- HOÀN TẤT THÀNH CÔNG ---");
            alert(`Áp dụng thành công! Giảm ${discountPercent}%`);

        } else {
            const errorMsg = await res.text();
            alert(errorMsg);
        }
    } catch (err) {
        // In lỗi chi tiết ra Console để bạn đọc
        console.error("LỖI XẢY RA:", err);
        alert("Lỗi hệ thống: " + err.message);
    }
}