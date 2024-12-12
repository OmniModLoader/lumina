# Lumina

Welcome to Lumina, a Java-based framework designed to simplify how you handle mappings, serialization, and
deserialization.
Whether you’re working with full mappings, compressed mappings, or something in between,
Lumina makes it easy, efficient, and reliable. It’s built with flexibility in mind, so you can integrate it into your
projects seamlessly.

## **What does Lumina do?**

This project is the mapping handler for [OmniMC](https://omnimc.org). We use this to control the mappings.

- **Extensible Design**:
    - Works with custom serializers like `LineSerializer` and `CompressedLineSerializer` for greater flexibility.

- **Robust Error Handling**:
    - The `FailedState` system handles errors gracefully and gives you all the info you need to debug.

- **Centralized Failure Consumers**:
    - Simplifies error management with classes like `AcceptConsumer` for reusable and streamlined handling.

## **Getting Started**

Here’s how to get Lumina up and running:

1. **Check your setup**: Make sure you’re using Java 21 or higher.
2. **Add Lumina to your project**:
    - Clone the repository:
    ```shell
       git clone https://github.com/<your-repo>/lumina.git
    ```
Or, include it as a dependency in your build system:

- **Maven**:
```xml
       <dependency>
         <groupId>org.omnimc</groupId>
         <artifactId>lumina</artifactId>
         <version>1.0.0</version>
       </dependency>
```

- **Gradle**
```groovy
       implementation 'org.omnimc:lumina:1.0.0'
```

## **How to Use**
### **Deserializing Mappings**
Lumina gives you specialized tools for deserialization, so you can adapt it to whatever kind of mapping format you’re working with.

#### Full Deserialization:
For handling complete mappings like classes, fields, and methods, use `FullDeserializer`:
```java
FullDeserializer deserializer = new FullDeserializer();
Mappings mappings = ...; // Your Mappings instance
File mappingsDir = new File("mappings/");

if (deserializer.deserializeToFile(mappings, mappingsDir)) {
    System.out.println("Full mappings deserialized successfully!");
}
```

### **Understanding Mapping Types**
`MappingType` indicates what kind of mapping you're working with. Each type is tied to a specific serializer:
- `FULL`: For uncompressed mappings.
- `COMPRESSED`: Uses the `CompressedLineSerializer` for compressed data.
- `PARAMETERS`: Specifically for parameter mappings.
- `UNKNOWN`: For scenarios where the mapping type isn’t defined.

#### Example usage
```java
MappingType type = MappingType.FULL;
LineSerializer serializer = type.getLineSerializer();
System.out.println("Serializer for FULL: "+serializer);
```

### **Error Handling That Works for You**
If something goes wrong, Lumina has you covered with `FailedState`. You can create meaningful error states and handle them however you see fit:

```java
FailedState error = FailedState.of("Failed to deserialize", ParameterDeserializer.class);
System.err.println(error);
```
And if you want to automate failure handling, you can set up custom consumers with `AcceptConsumer`:
```java
AcceptConsumer consumer = new AcceptConsumer();
consumer.setConsumer(failedState -> {
    System.err.println("Error occurred: " + failedState);
});
```

## **Contributing**
We love contributions! Here’s how you can help grow Lumina:
1. Fork the repo.
2. Create a new branch for your feature or fix.
3. Submit a pull request with a clear explanation of your changes.

Every contribution is appreciated, no matter how big or small.
## **License**
This project is licensed under the [MIT License](LICENSE). That means you can use, modify, and distribute it freely, as long as you give credit where it’s due.