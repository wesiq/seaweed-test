package test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HadoopAccessTest {

    @Test
    void shouldWriteAndReadLargeFile() throws IOException {
        var configuration = new Configuration();
        configuration.set("fs.s3a.access.key", "some_access_key1");
        configuration.set("fs.s3a.secret.key", "some_secret_key1");
        configuration.set("fs.s3a.endpoint", "http://localhost:8333");
        configuration.set("com.amazonaws.services.s3a.enableV4", "true");
        configuration.set("fs.s3a.path.style.access", "true");
        configuration.set("fs.s3a.connection.ssl.enabled", "false");

        var largeFilePath = new Path("s3a://test-bucket/" + UUID.randomUUID());

        var fileSystem = FileSystem.get(largeFilePath.toUri(), configuration);

        var randomBytesSize = 16;
        var writeBytes = randomBytes(randomBytesSize);
        try (var outputStream = fileSystem.create(new Path(largeFilePath, "file.tmp"))) {
            outputStream.write(writeBytes);
        }

        byte[] readBytes = new byte[randomBytesSize];
        try (var inputStream = fileSystem.open(new Path(largeFilePath, "file.tmp"))) {
            inputStream.readFully(readBytes);
        }

        assertThat(readBytes).isEqualTo(writeBytes);
    }

    private byte[] randomBytes(int size) {
        byte[] randomBytes = new byte[size];
        Random secureRandom = new Random();
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

}
