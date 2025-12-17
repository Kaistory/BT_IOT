const mqtt = require('mqtt');

const THINGSBOARD_HOST = 'thingsboard.cloud';
const PROVISION_KEY = 'y2pftybpc4iv33weoasl';
const PROVISION_SECRET = 'idfh3w2lp76kyvsrwf72';
const DEVICE_NAME = 'My_NodeJS_Device';

const client = mqtt.connect(`mqtt://${THINGSBOARD_HOST}`, {
    username: 'provision'
});

client.on('connect', () => {
    console.log("Đã kết nối tới server để Provisioning...");

    client.subscribe('/provision/response');

    const provisionRequest = {
        deviceName: DEVICE_NAME,
        provisionDeviceKey: PROVISION_KEY,
        provisionDeviceSecret: PROVISION_SECRET
    };

    client.publish('/provision/request', JSON.stringify(provisionRequest));
    console.log("Đã gửi yêu cầu đăng ký...");
});

client.on('message', (topic, message) => {
    if (topic === '/provision/response') {
        const response = JSON.parse(message.toString());
        console.log("Phản hồi từ server:", response);

        if (response.status === 'SUCCESS') {
            console.log("Đăng ký thành công!");
            console.log("ACCESS_TOKEN của thiết bị là:", response.credentialsValue);
            client.end();
        } else {
            console.error("Đăng ký thất bại!");
        }
    }
});