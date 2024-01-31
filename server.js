const express = require('express');
const bodyParser = require('body-parser');
const fs = require('fs');
const path = require('path');
const os = require('os');

const app = express();
const PORT = 9000;

// Middleware to parse JSON body
app.use(bodyParser.json());

// POST endpoint to receive exception data
app.post('/api/exceptions', (req, res) => {
    const exceptionData = req.body;

    // Add additional fields for more detailed exception logging
    exceptionData.timestamp = new Date().toISOString();
    exceptionData.serverName = require('os').hostname();

    // Convert the data to a string for file storage
    const dataString = JSON.stringify(exceptionData, null, 2);

    // Create a unique filename for each exception
    const filename = `exceptions_report_${Date.now()}.json`;

    // Save the file in a 'logs' directory
    const filePath = path.join(__dirname, 'logs', filename);
    fs.writeFile(filePath, dataString, (err) => {
        if (err) {
            console.error('Error saving exception:', err);
            res.status(500).send('Error saving exception');
            return;
        }
        console.log(`Exception logged: ${filename}`);
        res.status(200).send(`Exception logged: ${filename}`);
    });
});

// Create 'logs' directory if it doesn't exist
const logsDir = path.join(__dirname, 'logs');
if (!fs.existsSync(logsDir)) {
    fs.mkdirSync(logsDir);
}

// Start the server
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);

    // Print the local IP addresses
    const ifaces = os.networkInterfaces();
    Object.keys(ifaces).forEach(ifname => {
        let alias = 0;

        ifaces[ifname].forEach(iface => {
            if ('IPv4' !== iface.family || iface.internal !== false) {
                // Skip over internal (i.e. 127.0.0.1) and non-IPv4 addresses
                return;
            }

            if (alias >= 1) {
                // This single interface has multiple IPv4 addresses
                console.log(`${ifname}:${alias}, ${iface.address}`);
            } else {
                // This interface has only one IPv4 address
                console.log(`${ifname}, ${iface.address}`);
            }
            ++alias;
        });
    });
});
