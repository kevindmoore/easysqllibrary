# EasySQLLibrary
This library makes using SQLite in Android very easy. You can create a database with 1 line of code and your data models.  
To start using the library add the following to your build.gradle file (not the root file):  
```
    compile "com.mastertechsoftware.easysqllibrary:easysqllibrary:1.0.0"
```  
### Data Models
Data models are just POJOs (Plain Old Java Objects). The can subclass any object but must implement ReflectTableInterface. If you would like to subclass a default interface, use the DefaultReflectTable class. EasySQLLibrary will use reflection to pull out the field names for the database. Each POJO will be a table and each field will be a field in the table. You will make 1 create call for each database, passing in all the models you want for that database.  
Example:  

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

This creates Database named Slack with the main table name of Users with two tables named user and profile.  

### Get table values  
To get all of the items from a table use the following:  

```
databaseHelper.getAll(User.class);  
```

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
To remove all items call:  

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