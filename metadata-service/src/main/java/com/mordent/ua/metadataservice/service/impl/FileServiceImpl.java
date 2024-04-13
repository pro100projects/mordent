package com.mordent.ua.metadataservice.service.impl;

import com.mordent.ua.metadataservice.model.data.SongMetadata;
import com.mordent.ua.metadataservice.service.FileService;
import lombok.RequiredArgsConstructor;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Override
    public Mono<SongMetadata> getSongMetadata(final Long id, final String filepath) {
        File file = new File(filepath);
        try (InputStream input = new FileInputStream(file)) {
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);

            return Mono.just(new SongMetadata(id, Arrays.stream(metadata.names()).collect(Collectors.toMap(
                    key -> (key.contains(":") && key.split(":").length > 1) ? key.split(":")[1] : key,
                    metadata::get,
                    (existingValue, newValue) -> existingValue.trim().isEmpty() ? newValue : existingValue
            ))));
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        }
        return Mono.empty();
    }
}
