# Soduku-Solving

This program is a collaborative effort to implement and improve on theoretical algorithms for solving Sudoku puzzles using graph algorithms.

## Overview

In this project, we aim to take the theoretical work from a research paper on solving standard Sudoku puzzles using Breadth-First Search (BFS) and Depth-Limited Search (DLS), and turn it into a practical, enhanced implementation. The goal is to replicate their results and explore potential improvements to their approach.

The research paper can be found here:  
[Comparison Analysis of Breadth First Search and Depth Limited Search Algorithms in Sudoku Game](https://www.researchgate.net/publication/358642884_Comparison_Analysis_of_Breadth_First_Search_and_Depth_Limited_Search_Algorithms_in_Sudoku_Game).

## Requirements

### Core Implementation
1. **Group Collaboration**: Work with an assigned partner to complete the project.
2. **Algorithm Implementation**:
    - Use a graph data structure to represent the Sudoku puzzle.
    - Implement BFS and a variation of DFS (Depth-Limited Search) to solve the puzzle.
3. **Citation**: Add a proper citation for the research paper in the program header.
4. **Solution Tracking**:
    - Track solutions found or not found.
    - Note that BFS may find multiple solutions for a puzzle.
5. **Libraries and References**:
    - Java libraries may be used for graph implementation.
    - You may start with an existing Sudoku solver using graphs, but be sure to cite your source.

### Improvement Options
Choose one of the following enhancements:
- **Grid-Size Independence**: Adjust the algorithm to work on grids of any size while maintaining compatibility with standard computers (e.g., use threading or memory management).
- **3D Sudoku Compatibility**: Adapt the algorithm to solve 3D Sudoku puzzles.
- **Algorithm Combination**: Explore the combination of BFS and DLS (as used in solving Rubik's cubes in [this paper](https://www.researchgate.net/publication/326749335_Solving_Rubik%27s_Cube_Using_Graph_Theory_ICCI-2017)) and apply it to Sudoku.

### Summary
Write a brief (1-page max) summary addressing:
- Does your improvement work consistently or only under certain conditions?
- Efficiency in terms of time and space.
- Suitability for finding fast solutions versus multiple solutions.
- Rationale for your approach and results.

## Rubric

### Functionality
- **10 pts**: Reads in a file of easy, medium, and hard Sudoku puzzles for testing.
- **20 pts**: Uses a graph to solve puzzles.
- **20 pts**: Applies BFS to each puzzle.
- **5 pts**: Tracks solutions, including multiple solutions.
- **20 pts**: Applies DLS to each puzzle.
- **5 pts**: Tracks solutions, including multiple solutions.

### Improvements
- **40 pts**: Implements one of the suggested improvements.
- **10 pts**: Improvement is well-commented, with clear documentation of adjustments and their rationale.

### Documentation
- **30 pts**: Results summary clearly explains the approach, functionality, strengths, and limitations.
- **5 pts**: Proper citation of the research paper and any borrowed code.
- **5 pts**: Comments and code style meet professional standards.

## Instructions

1. **Setup**: Clone the repository and set up your development environment.
2. **File Input**: Create a set of Sudoku puzzles (easy, medium, and hard) for testing.
3. **Implementation**: Follow the requirements to implement and improve the algorithms.
4. **Testing**: Validate your implementation using the test files and document your results.
5. **Summary**: Write the results summary and ensure all documentation and citations are included.

## Contribution
This project is a collaborative assignment. Be sure to document your contributions clearly in the code and report.

---

> **Note**: This project is a potential portfolio addition. Ensure your code is clean, well-commented, and adheres to best practices.