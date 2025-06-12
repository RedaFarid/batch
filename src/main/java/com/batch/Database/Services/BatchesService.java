package com.batch.Database.Services;

import com.batch.DTO.BatchSystemDataDefinitions.BatchModel;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Repositories.BatchesRepository;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(
        isolation = Isolation.SERIALIZABLE,
        propagation = Propagation.REQUIRES_NEW
)
public class BatchesService {
    private static final Logger log = LogManager.getLogger(BatchesService.class);
    private final BatchesRepository batchesRepository;

    public BatchesService(final BatchesRepository batchesRepository) {
        this.batchesRepository = batchesRepository;
    }

    public Optional<Batch> save(Batch batch) {
        return this.startMarshalling(batch.getModel()).map((rowModel) -> {
            batch.setRowModel(rowModel);
            return this.batchesRepository.save(batch);
        });
    }

    public Optional<Batch> findById(Long id) {
        return this.batchesRepository.findById(id).flatMap((batch) -> this.startUnMarshalling(batch.getRowModel()).map((batchModel) -> {
            batch.setModel(batchModel);
            return batch;
        }));
    }

    public void updateBatchControlOrder(long batchId, String order) {
        this.batchesRepository.updateBatchControlOrder(batchId, order);
    }

    public List<Batch> findAll() {
        return Lists.newArrayList(this.batchesRepository.findAll()).stream().flatMap((batch) -> this.startUnMarshalling(batch.getRowModel()).map((batchModel) -> {
            batch.setModel(batchModel);
            return batch;
        }).stream()).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public Optional<Batch> findByName(String batchName) {
        return this.batchesRepository.findByBatchName(batchName);
    }

    private Optional<String> startMarshalling(BatchModel model) {
        try {
            StringWriter sw = new StringWriter();
            JAXBContext jaxbcontext = JAXBContext.newInstance(BatchModel.class);
            Marshaller marshaller = jaxbcontext.createMarshaller();
            marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
            marshaller.marshal(model, sw);
            return Optional.ofNullable(sw.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<BatchModel> startUnMarshalling(String model) {
        try {
            JAXBContext jaxbcontext = JAXBContext.newInstance(BatchModel.class);
            Unmarshaller unMarshaller = jaxbcontext.createUnmarshaller();
            return Optional.ofNullable((BatchModel) unMarshaller.unmarshal(new StringReader(model)));
        } catch (Exception var4) {
            return Optional.empty();
        }
    }

    public void updateEndTime(Long id, LocalDateTime now) {
        this.batchesRepository.updateEndTime(id, now);
    }
}
