package org.maciejszuwarowski.infrastructure.collectionbox.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.maciejszuwarowski.domain.collectionbox.CollectionBoxFacade;
import org.maciejszuwarowski.domain.collectionbox.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/collection-boxes")
@RequiredArgsConstructor
public class CollectionBoxController {
    private final CollectionBoxFacade collectionBoxFacade;
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CollectionBoxInfoMessage registerCollectionBox() {
        return collectionBoxFacade.createCollectionBoxWithoutAssigning();
    }

    @PostMapping("/{collectionBoxId}/assign")
    public ResponseEntity<CollectionBoxInfoMessage> assignCollectionBox(
            @PathVariable String collectionBoxId,
            @RequestParam String fundraisingEventId) {
        CollectionBoxInfoMessage message = collectionBoxFacade.assignCollectionBox(collectionBoxId, fundraisingEventId);
        return ResponseEntity.ok(message);
    }
    @GetMapping
    public ResponseEntity<List<CollectionBoxPublicInfoDto>> listAllCollectionBoxes() {
        List<CollectionBoxPublicInfoDto> boxes = collectionBoxFacade.getAllCollectionBoxes();
        return ResponseEntity.ok(boxes);
    }
    @DeleteMapping("/delete/{collectionBoxId}")
    public ResponseEntity<CollectionBoxInfoMessage> unregisterCollectionBox(@PathVariable String collectionBoxId) {
        CollectionBoxInfoMessage message = collectionBoxFacade.unregisterCollectionBox(collectionBoxId);
        return ResponseEntity.ok(message);
    }
    @PostMapping("/{collectionBoxId}/add-funds")
    public ResponseEntity<TransferResultDto> addFundsToCollectionBox(
            @PathVariable String collectionBoxId,
            @Valid @RequestBody MoneyDto moneyDto) {
        TransferResultDto result = collectionBoxFacade.addMoneyToCollectionBox(collectionBoxId, moneyDto);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/register-and-assign")
    @ResponseStatus(HttpStatus.CREATED)
    public CollectionBoxInfoMessage createAndAssign(@RequestParam String fundraisingEventId) {
        return collectionBoxFacade.createAndAssignCollectionBox(fundraisingEventId);
    }
}
