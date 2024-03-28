package com.project.habitasse.domain.offer.resource;

import com.project.habitasse.domain.offer.entities.request.OfferRequest;
import com.project.habitasse.domain.offer.service.OfferService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/offer")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @PostMapping("/save")
    public ResponseEntity<?> registerDemand(@RequestBody OfferRequest offerRequest, HttpServletRequest request) throws Exception {
        if (offerRequest.getDemandId() == null || StringUtils.isEmpty(offerRequest.getText()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");

        return ResponseEntity.ok(offerService.saveOffer(offerRequest, request.getHeader("Authorization")));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePropertyDemand(@PathVariable Long id, @RequestBody OfferRequest offerRequest) throws Exception {
        if (id == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID inválido");

        if (offerRequest == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");

        return ResponseEntity.ok(offerService.updateOffer(id, offerRequest));
    }
}
