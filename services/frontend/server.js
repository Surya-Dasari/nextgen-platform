const express = require("express");
const path = require("path");

const app = express();
const PORT = 9080;

app.use(express.json());
app.use(express.static(path.join(__dirname, "public")));

app.get("/health", (req, res) => res.send("OK"));

app.listen(PORT, () => {
  console.log(`NextGen UI running on port ${PORT}`);
});
