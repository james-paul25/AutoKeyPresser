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
   cd AutoKeyPresser
