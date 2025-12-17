const axios = require('axios');

const THINGSBOARD_HOST = 'thingsboard.cloud';
const ACCESS_TOKEN = 'D7hNd20xNwj21lwHINC9';

const url = `https://${THINGSBOARD_HOST}/api/v1/${ACCESS_TOKEN}/telemetry`;

async function sendTelemetry() {
    try {
        const telemetryData = {
            pressure: Math.floor(Math.random() * 100) + 900,
            active: true
        };

        const response = await axios.post(url, telemetryData);

        if (response.status === 200) {
            console.log("Đã gửi HTTP thành công:", telemetryData);
        }
    } catch (error) {
        console.error("Lỗi khi gửi HTTP:", error.message);
    }
}

setInterval(sendTelemetry, 3000);