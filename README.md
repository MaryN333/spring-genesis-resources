### **Zadání**

Obrací se na tebe správní rada společnosti **Genesis Resources**. Tvým úkolem je vytvořit pro ni registrační systém, aby mohla suroviny distribuovat opravdu spravedlivě.

### Naše zadání projektu:

1. aplikace bude obsahovat REST rozhraní
2. data se budou ukládat do databáze
3. je požadované správné rozdělení kódu (controller, service atd.)
4. důležité je také dbát na čistotu kódu
5. odevzdaní musí obsahovat export z Postmana (kterým je testované API)
6. odevzdání musí ještě obsahovat SQL příkazy, kterými byla vygenerována databáze

### POŽADAVKY NA API

### **Požadované operace**

1. založení nového uživatele
   - Založit nového uživatele je možné přes api: `POST api/v1/users`
   - Informace o novém uživateli budou předány skrz objekt
     ```
     { name: string, surname: string, personID: string(12) }
     ```
   - `personID` je jedinečné a je generované pro každého obyvatele. Můžete si představit, že je to něco jako rodné číslo, které je také více méně jedinečné. `PersonID` je do našeho systému poskytováno skrz nějakou certifikační autoritu. My si to nasimulujeme tak, že v našem projektu vytvoříme soubor (například `dataPersonId.txt` a do něho zapíšeme tyto hodnoty:
     ```
     jXa4g3H7oPq2
     yB9fR6tK0wLm
     cN1vZ8pE5sYx
     tQdG2kP3mJfB
     iM5sO6zXcW7v
     rU8nA9eT2bYh
     wV6eH1fK7qZj
     sL4gN9dC3bXz
     kR0aZ7vW2nDl
     eI1oY6tQ9dKj
     gT4cR7wS0lVx
     xF9hD2yJ3sWv
     hM5bZ8nK4aVf
     qE3lY6uT0vKd
     bG2zC7jR9xVp
     vB1fX4rH7iNt
     aO8kP3mZ6dIw
     dW9pL2eU1yNc
     nS7tJ0qR5wGh
     mY6sT1jA3cLz
     ```
   - Ještě je nutné říci, že v databázi musí být `personID` jedinečné. Tz. že při každém zápisu musíme nějakým způsobem zkontrolovat, zda `personID` nebylo přiřazeno již jinému záznamu.
   - **UUID - Generování**
     
     Pro každý nový záznam musí být vygenerovaný ještě UUID, což bude další jedinečný identifikátor uživatele. Na vygenerování můžeš použít například následující knihovnu ([https://www.baeldung.com/java-uuid](https://www.baeldung.com/java-uuid)).

2. informace o uživateli
   - dostat informace o jednom uživateli je možné přes api: `GET api/v1/users/{ID}`
     
     Tento request vrátí následující objekt:
     ```
     { id: int, name: string, surname: string }
     ```
   - ještě existuje varianta `api/v1/users/{ID}?detail=true`
     
     Kdy request vrátí rozšířený objekt:
     ```
     { id: int, name: string, surname: string, personID: string, uuid: string }
     ```

3. informace o všech uživatelích
   - dostat informace o všech uživatelích je možné přes api: `GET api/v1/users`
     
     Tento request vrátí následující objekt:
     ```
     List <{id: int, name: string, surname: string }>
     ```
   - ještě existuje varianta `api/v1/users?detail=true`
     
     Kdy request vrátí rozšířený objekt:
     ```
     {id: int, name: string, surname: string, personID: string, uuid: string }
     ```

4. upravit informace o uživateli
   - upravit uživatele je možné přes api: `PUT api/v1/users`
     
     Informace o novém uživateli budou předány skrz objekt:
     ```
     {id: int, name: string, surname: string }
     ```
     Z toho tedy je jasné, že je možné upravit pouze name a surname

5. smazat uživatele
   - ještě existuje varianta `DELETE api/v1/users/{ID}`

### POŽADAVKY NA DATABÁZI

### **Struktura**

| Jméno sloupce | Typ | Další Info |
|---------------|-----|------------|
| ID | Long | PrimaryKey, Unique, NotNull, Autoincrement |
| Name | Varchar | NotNull |
| Surname | Varchar | |
| PersonID | Varchar | Unique, NotNull |
| Uuid | Varchar | Unique, NotNull |

### Doplňující možnosti

Pokud chceš udělat něco navíc :), můžeš si vybrat z následujících možností:

- **logovací** framework
- front-end
- JUnit
- vytvořit dva profily

### Doplňující informace

Po domluvě s lektorem byla přidána entita pracovní pozice (Position), aby bylo možné procvičit vztah ManyToMany. Jeden uživatel může mít více pracovních pozic a jednu pracovní pozici může mít více uživatelů.

Projekt také obsahuje:
- JUnit testy
- Front-end aplikaci

V repozitáři ve složce resources najdete export z Postmanu a databázové skripty.
