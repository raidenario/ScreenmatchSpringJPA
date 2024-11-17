# ScreenMatch - Series and Episode Manager

This project is a Series and Episode Manager that integrates with the OMDb API to search for and store information about series and episodes in a database. Developed using the Spring Framework and JPA (Java Persistence API), the application provides a command-line interface that allows users to search, filter, and manage series, seasons, and episodes.

## Key Features
- **Series Search via API**: Enables searching for series directly from the OMDb API and storing them in the database.
- **Season and Episode Management**: Retrieves and organizes episodes by season, associating them with the corresponding series.
- **Filtering and Listing**: Includes options to list and filter series by criteria such as rating, actor, category, number of seasons, and more.
- **Top 5 Series and Episodes**: Displays the top-rated series and episodes.
- **Advanced Search**: Search for episodes by name fragments, release date, or specific series.

## Technologies Used
- **Java**: The main programming language of the project.
- **Spring Framework**: Used for controlling the application logic and integrating with the OMDb API.
- **Spring Data JPA**: Facilitates object-relational mapping (ORM) and persistence in relational databases.
- **OMDb API**: A data service used to fetch information about series and episodes.
- **JPA/Hibernate**: A persistence framework that abstracts database operations and manages entities (series, seasons, episodes).

## Project Structure
- **ConsumoApi and ConverterDados**: Classes responsible for consuming and converting data from the OMDb API.
- **Series Repository (SerieRepository)**: An interface that manages database access, using JPA to perform queries, filtering, and persistence of series and episodes.
- **Entity Model**: Represents series, seasons, and episodes as persistent objects.

## Additional Features
- Listing series by category.
- Filtering series based on the number of seasons and minimum rating.
- Searching for episodes by release date or name fragment.

## How to Run
1. Clone this repository.
2. Set up the database in the `application.properties` file.
3. Run the application with the command:
   ```bash
   mvn spring-boot:run
