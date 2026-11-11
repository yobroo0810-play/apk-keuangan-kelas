const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const APK_PATH = '/app/applet/.build-outputs/app-debug.apk';
const HTML_PATH = '/app/applet/web/index.html';

const server = http.createServer((req, res) => {
    const url = req.url.split('?')[0];

    // Download APK endpoint
    if (url === '/download' || url === '/download-apk' || url === '/KasKelas-PRO.apk' || url === '/app-debug.apk') {
        if (fs.existsSync(APK_PATH)) {
            const stat = fs.statSync(APK_PATH);
            res.writeHead(200, {
                'Content-Type': 'application/vnd.android.package-archive',
                'Content-Disposition': 'attachment; filename="KasKelas-PRO.apk"',
                'Content-Length': stat.size
            });
            const readStream = fs.createReadStream(APK_PATH);
            readStream.pipe(res);
            return;
        } else {
            res.writeHead(404, { 'Content-Type': 'text/plain' });
            res.end('APK not found. Please compile first.');
            return;
        }
    }

    // Serve HTML
    if (url === '/' || url === '/index.html') {
        if (fs.existsSync(HTML_PATH)) {
            res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
            res.end(fs.readFileSync(HTML_PATH, 'utf-8'));
            return;
        }
    }

    res.writeHead(404, { 'Content-Type': 'text/plain' });
    res.end('404 Not Found');
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`KasKelas Web & APK Server listening on http://0.0.0.0:${PORT}`);
});
