# Sudoku Solving Project

## Overview

This project involves implementing and improving a theoretical algorithm for solving standard Sudoku puzzles using graph algorithms. You'll work with a partner to develop, test, and refine the solution, which is based on the research paper:

- **Paper**: [Comparison Analysis of Breadth-First Search and Depth-Limited Search Algorithms in Sudoku Game](https://www.researchgate.net/publication/358642884_Comparison_Analysis_of_Breadth_First_Search_and_Depth_Limited_Search_Algorithms_in_Sudoku_Game)

Your task includes implementing their algorithm and enhancing it to solve new challenges.

---

## Requirements

1. **Collaboration**: Work with a partner to complete the project.
2. **Algorithm Implementation**:
   - Use a graph data structure.
   - Implement Breadth-First Search (BFS).
   - Implement a variation of Depth-First Search (DFS), known as Depth-Limited Search (DLS).
3. **Citation**:
   - Properly cite the research paper in your header.
   - Cite any additional code sources used.
4. **Testing**:
   - Track the solutions found (or not found) for various puzzles.
   - Ensure the program handles multiple solutions as per BFS capabilities.
5. **Improvement Options** (choose one):
   - Adjust the approach to work with varying grid sizes on a standard computer, potentially using threading or memory management techniques.
   - Adapt the algorithm for 3D Sudoku puzzles.
   - Combine BFS and DLS to improve the solving process (inspired by Rubik’s cube solving techniques).
6. **Summary Report**:
   - Write a brief (1-page max) summary explaining your improvements:
      - Does it work universally or selectively?
      - Is it efficient (time/space)?
      - Is it optimized for speed or finding multiple solutions?
      - Provide justifications for your approach.

---

## Rubric

### Implementation
- **10 pts**: Reads a file of easy, medium, and hard Sudoku puzzles for testing.
- **20 pts**: Solves puzzles using a graph data structure.
- **20 pts**: Applies BFS to each puzzle.
- **5 pts**: Tracks solutions, including multiple solutions.
- **20 pts**: Applies DLS to each puzzle.
- **5 pts**: Tracks solutions, including multiple solutions.

### Improvement
- **40 pts**: Implements one of the suggested improvements.
- **10 pts**: Clearly comments on the improvement, including adjustments and reasons.

### Documentation
- **30 pts**: Summarizes results, including approach, efficiency, and limitations.
- **5 pts**: Proper citation of research paper and borrowed code.
- **5 pts**: Overall comments and code style.

---

## Getting Started

1. **Setup**:
   - Clone this repository and set up your development environment.
   - Ensure you have access to Java libraries for graphs, BFS, and DFS/DLS.

2. **File Requirements**:
   - Create test files containing Sudoku puzzles (easy, medium, hard).
   - Format test files appropriately for the program to read.

3. **Development**:
   - Implement the base algorithms (BFS, DLS).
   - Enhance the solution based on your chosen improvement.

4. **Testing**:
   - Run the program against the test files.
   - Log results, including performance metrics and solution statistics.

5. **Documentation**:
   - Include detailed comments in your code.
   - Prepare the one-page summary of your approach and findings.

---

## Additional Resources

- **Research Papers**:
   - [Comparison Analysis of BFS and DLS in Sudoku Game](https://www.researchgate.net/publication/358642884_Comparison_Analysis_of_Breadth_First_Search_and_Depth_Limited_Search_Algorithms_in_Sudoku_Game)
   - [Solving Rubik’s Cube Using Graph Theory](https://www.researchgate.net/publication/326749335_Solving_Rubik%27s_Cube_Using_Graph_Theory_ICCI-2017)

- **References**:
   - Russell, S., & Norvig, P. (2010). Artificial Intelligence: A Modern Approach (3rd Edition). Chapter 6: Constraint Satisfaction Problems. Specifically, sections on Minimum-Remaining-Values (MRV) and Least- 
    Constraining-Value (LCV) heuristics​

---

## License

This project is for educational purposes and intended to help participants build their portfolios. All cited works and borrowed code must be appropriately attributed.
