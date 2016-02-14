# EasySQLLibrary
This library makes using SQLite in Android very easy. You can create a database with 1 line of code and your data models.  
To start using the library add the following to your build.gradle file (not the root file):  
```
    compile "com.mastertechsoftware.easysqllibrary:easysqllibrary:1.0.2"
```  
### Data Models
Data models are just POJOs (Plain Old Java Objects). They can subclass any object but must implement ReflectTableInterface. If you would like to subclass a default interface, use the DefaultReflectTable class. EasySQLLibrary will use reflection to pull out the field names for the database. Each POJO will be a table and each field will be a field in the table. You will make 1 create call for each database, passing in all the models you want for that database. This is usually done in the application.  

Table Example:  

```  
public class Meta extends DefaultReflectTable {  
    protected int version;  
    protected String database;  
    protected String creationString;  
}  
```  

This will create a table named Meta with the fields version : INTEGER, database : TEXT and creationString : TEXT.  

### Create the database  
To create a dataase, simply call: 

```  
DatabaseHelper databaseHelper = new DatabaseHelper("Slack");  
databaseHelper.createDatabase("User", User.class, Profile.class);  
```

This creates a Database named Slack with the main table name of Users, with two tables named user and profile.  

### Get table values  
To get all of the items from a table, use the following:  

```
List<User> users = databaseHelper.getAll(User.class);  
```
To get a single item from a table, use the following (where id is an int that specifies a unique key):  

```
User user = databaseHelper.get(User.class, id);  
```

More advanced queries can made using the DatabaseManager class.

### Adding Items
To add a list of items call:  

``` 
databaseHelper.addAll(User.class, users);  
```

To add a single item call:  

```  
databaseHelper.add(User.class, user);  
```

### Removing Items
To remove all items in a table call:  

```
databaseHelper.removeAll(User.class);  
```

To remove a single item call: 

```
databaseHelper.delete(User.class, user);  
```

### Updating Items
To update a single item call: 

```
databaseHelper.update(User.class, user);  
```

### Deleting the database
To delete the entire database (if you were upgrading and needed to build it again): 

```
databaseHelper.deleteDatabase();  
```

### Proguard
EasySQLLibrary uses reflection so you will have to keep those classes in your proguard file
