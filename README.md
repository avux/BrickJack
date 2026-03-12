### BrickJack — Lego Mindstorms Blackjack robot

BrickJack is a Lego Mindstorms EV3 robot that **automates and moderates a variation of Blackjack using UNO cards**.  
**"BrickJack is a robot that manages a game of Blackjack."**

---

### Overview

BrickJack scans UNO cards with an EV3-mounted light sensor, recognizes the card value using OCR on a connected PC, and runs Blackjack game logic on the EV3 (the EV3 also acts as the dealer). The project was developed as part of the _Lego Mindstorms Praktikum 2021_ and follows a modular architecture so hardware and software components can be developed and tested independently.

---

### Key features

- **Automated dealing** — mechanical dispenser moves single UNO cards into a scanning tray.
- **Gantry scanner** — cartesian gantry moves the EV3 brightness sensor across the card to capture pixel data.
- **PC-assisted OCR** — scanned brightness data is sent to a PC; Tess4J/Tesseract is used to recognize digits.
- **Game moderation** — EV3 runs the Blackjack rules, tracks player scores, and accepts player input via EV3 buttons and a touch sensor.


---

### Hardware summary

- **Platform:** Lego Mindstorms EV3 (motors, touch sensors, brightness sensor, EV3 brick).
- **Gantry:** two-stage cartesian gantry for stable, repeatable scans; touch sensors used for homing/calibration.
- **Card dispenser & distributor:** wheel-based dispenser for single-card feed; lever/tire mechanism to eject cards to players or dealer.
- **User input:** EV3 buttons for player count; a ground-mounted touch sensor for hit/stand input.

---

### Software summary

- **EV3-side (Mindstorm):**
    - Controls motors, sensors, dispenser, distributor, and game logic (`Game`, `CardDealer`, `LegoScanner`, `MindClient`).
    - Sends raw scan data to the PC and receives recognized card values.
- **PC-side:**
    - `ServerPC` receives pixel arrays, processes them (`processPixelArrayData`) and runs OCR via **Tess4J** (Tesseract wrapper).
    - `ArrayToIntConverter` thresholds, cleans, scales images and calls Tesseract with a digits-only training set.
- **Communication:** `ConnectionParent` provides `readInt()`, `writeInt()`, `sendIntData()` and higher-level helpers for sending/receiving flattened 2D pixel arrays.

---

### How scanning & OCR works (concise)

1. Gantry moves the brightness sensor across the card and records brightness + tacho counts into a flattened array.
2. `processPixelArrayData()` reconstructs the 2D image, discards rows with >50% missing values, and fills small holes.
3. `ArrayToIntConverter` thresholds the image (histogram-based), removes edge artefacts and dithering, scales the image (experimentally 20% scale worked well), and runs Tesseract (digits-only) to return a single integer.

---

### Quick setup (developer notes)

- **Hardware:** assemble gantry, dispenser, distributor and connect EV3 motors/sensors as in the project photos. Calibrate homing touch sensors before first run.
- **EV3 software:** compile and deploy the Mindstorm Java project to the EV3 brick (project split into EV3-side and PC-side to avoid uploading Tess4J to the brick).
- **PC software:** run `ServerPC` to accept connections from the EV3 `MindClient`. Install Tess4J and the Tesseract digits-only training data. Ensure Java and required libraries are on the PC.
- **Run order:** start the PC server → start EV3 client → use EV3 buttons to set player count → play using the touch sensor for hit/stand.

---

### Repository layout

- `ev3/` — EV3-side code (motors, sensors, game logic).
- `pc/` — PC-side code (server, image processing, Tess4J integration).
- `docs/` — photos, videos, flowcharts, and this documentation.
- `images/` — sample scans (raw and cleaned).
- `LICENSE` — project license.
- `README.md` — this file (place at repo root).

# BrickJack
Overleaf: https://www.overleaf.com/1273331577npttwjxrsbsk
