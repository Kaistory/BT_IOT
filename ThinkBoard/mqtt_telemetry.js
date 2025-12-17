const mqtt = require('mqtt');

const THINGSBOARD_HOST = 'thingsboard.cloud';
const ACCESS_TOKEN = 'D7hNd20xNwj21lwHINC9';

const client = mqtt.connect(`mqtt://${THINGSBOARD_HOST}`, {
    username: ACCESS_TOKEN
});

client.on('connect', () => {
    console.log("Đã kết nối MQTT để gửi dữ liệu!");

    setInterval(() => {
        const telemetryData = {
            temperature: Math.floor(Math.random() * 30) + 20,
            humidity: Math.floor(Math.random() * 40) + 40
        };

        client.publish('v1/devices/me/telemetry', JSON.stringify(telemetryData));
        console.log("Đã gửi MQTT:", telemetryData);
    }, 2000);
});