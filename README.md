# AutoKeyPresser

A lightweight Java tool for Linux Mint that repeatedly simulates keypress events (e.g., `3 → 2 → 4 → 1` sequence), designed for easy automation (e.g., in AQW farming).

---

##  Features

- Simple Java-based auto key presser using `java.awt.Robot`
- Configurable key sequence (default: 3, 2, 4, 1)
- Customizable delays between key presses and loops
- Works well on Linux Mint / X11 environments
- Easy to run from the terminal

---

##  Prerequisites

- Java Development Kit (JDK) installed (e.g., `openjdk-11-jdk`)
- A Linux environment using X11 (necessary for key simulation)

---

##  Installation & Usage

1. **Clone the repository**:
   ```bash
   git clone https://github.com/james-paul25/AutoKeyPresser.git
   cd AutoKeyPresser/src

2. **Compile the source:**:
   ```bash
   javac AutoKeyPresser.java


3. **Run the program:**:
   ```bash
   java AutoKeyPresser

- Make sure your target application is focused when the script runs.

4. **Termination option:**:
   ```bash
   pkill -f AutoKeyPresser
   ```
   - or type ctrl + c in the terminal

## Author
- Created by james‑paul25 — designed for effortless automation via Java key pressing.


