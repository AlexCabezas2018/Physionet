function fn() {

    var driverConfigs = {
        'chromium-linux':{ type: 'chrome', executable: '/usr/bin/chromium-browser', showDriverLog: true },
        'chrome-windows':{ type: 'chrome', showDriverLog: true }
    }

    var config = {
        baseUrl : 'http://localhost:8080',
        driverConfig: driverConfigs['chromium-linux']
    };

    return config;
}