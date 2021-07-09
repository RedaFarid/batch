package com.batch.Database.Services;

import com.batch.DTO.BatchSystemDataDefinitions.BatchModel;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Repositories.BatchesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class BatchesService {

    private final BatchesRepository batchesRepository;

    @CacheEvict(value = "batches", cacheManager = "cacheManagerForBatches")
    public Batch save(Batch batch) {
        final String rowModel = toRowModel(batch.getModel());
        batch.setRowModel(rowModel);
        return batchesRepository.save(batch);
    }

    @Cacheable(value = "batches", cacheManager = "cacheManagerForBatches")
    public Optional<Batch> findById(Long id) {
        return batchesRepository
                .findById(id)
                .map(batch -> {
                    final BatchModel batchModel = toModel(batch.getRowModel());
                    batch.setModel(batchModel);
                    return batch;
                });
    }

    @Cacheable(value = "batches", cacheManager = "cacheManagerForBatches")
    public List<Batch> findAll(){
        return batchesRepository.findAll().stream().peek(batch -> {
            final BatchModel batchModel = toModel(batch.getRowModel());
            batch.setModel(batchModel);
        }).collect(Collectors.toList());
    }

    @Cacheable(value = "batches", cacheManager = "cacheManagerForBatches")
    public Optional<Batch> findByName(String batchName) {
        return batchesRepository.findByBatchName(batchName);
    }



    //Managing XML
    public BatchModel toModel(String rowModel) {
        BatchModel batchModel = new BatchModel();
        try {
            batchModel = startUnMarshalling(rowModel);
        } catch (Exception ignored) {

        }
        return batchModel;
    }
    public String toRowModel(BatchModel model) {
        String s = "";
        try {
            s = startMarshalling(model);
        } catch (Exception ignored) {
        }
        return s;
    }

    private String startMarshalling(BatchModel model) throws Exception {
        StringWriter sw = new StringWriter();
        JAXBContext jaxbcontext = JAXBContext.newInstance(BatchModel.class);
        Marshaller marshaller = jaxbcontext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.marshal(model, sw);
        return sw.toString();
    }
    private BatchModel startUnMarshalling(String model) throws Exception {
        JAXBContext jaxbcontext = JAXBContext.newInstance(BatchModel.class);
        Unmarshaller unMarshaller = jaxbcontext.createUnmarshaller();
        return ((BatchModel) unMarshaller.unmarshal(new StringReader(model)));
    }


    public void updateBatchControlOrder(long batchId, String order) {
        batchesRepository.updateBatchControlOrder(batchId, order);
    }
}
